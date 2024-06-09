package Autowired.WebScoket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebScoketMannger implements Closeable, Runnable
{
    String ServerId;
    OutputStream outputStream;
    InputStream inputStream;
    Socket socket;

    public String getServerId()
    {
        return ServerId;
    }
    public OutputStream getOutputStream()
    {
        return outputStream;
    }
    public InputStream getInputStream()
    {
        return inputStream;
    }


    public static WebSocketHandler WebSocketHandler;
    private static final Map<String, WebScoketMannger> instances = new ConcurrentHashMap<>();

    public static WebScoketMannger getInstance(String ServerId)
    {
        return instances.get(ServerId);
    }

    public WebScoketMannger(String ServerId, Socket socket, OutputStream outputStream)
    {
        try
        {
            this.inputStream = socket.getInputStream();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.ServerId = ServerId;
        this.socket = socket;
        this.outputStream = outputStream;

        instances.put(ServerId, this);
        WebSocketHandler.onCreated(this,ServerId);
    }

    @Override
    public void close()
    {
        try
        {
            WebSocketHandler.onClose(this);
            socket.close();
        }
        catch (Exception e)
        {
            WebSocketHandler.onError(this);
            System.err.println(e.getMessage());
            System.err.println(socket + ":关闭失败");
        }
    }

    public void send(String message) throws IOException
    {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int length = messageBytes.length;
        // 构建帧头部
        byte[] frameHeader;
        if (length <= 125)
        {
            // 长度小于或等于125字节，使用单个字节表示长度
            frameHeader = new byte[]{(byte) (0x80 | 0x1), (byte) length};
        }
        else
        {
            // 长度大于125字节，使用额外的两个字节表示长度
            frameHeader = new byte[]{(byte) (0x80 | 0x1), (byte) 126,
                    (byte) ((length >> 8) & 0xFF), (byte) (length & 0xFF)};
        }
        // 将帧头部和消息文本合并
        byte[] frame = new byte[frameHeader.length + messageBytes.length];
        System.arraycopy(frameHeader, 0, frame, 0, frameHeader.length);
        System.arraycopy(messageBytes, 0, frame, frameHeader.length, messageBytes.length);
        // 发送帧
        outputStream.write(frame);
        outputStream.flush();
    }


    @Override
    public void run()
    {
        //不断回调他
        try
        {
            while (true)
            {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    String s = processWebSocketData(buffer, bytesRead, this);
                    if (s == null)
                    {
                        break;
                    }
                    WebSocketHandler.onMessage(this, s);
                    //回调他
                    // 处理完一个或多个帧后，可以在这里执行其他操作
                }
            }
        }
        catch (Exception e)
        {
            WebSocketHandler.onError(this);
            System.err.println(e.getMessage());
            System.err.println("在WebSocket里读取传入的数据的时候出错，或者客户端关闭了对应的Socket");
            WebScoketMannger remove = instances.remove(ServerId);
            remove.close();
        }
    }

    private static String processWebSocketData(byte[] buffer, int bytesRead, WebScoketMannger webScoketMannger)
    {
//        if (bytesRead < 2)
//        {
//            return false; // 数据不完整，需要更多数据
//        }
//        // 第一个字节：FIN, RSV, 操作码
//        // 操作码为1表示文本帧
//        boolean isTextFrame = (buffer[0] & 0x0F) == 0x01;
//        // 第二个字节：掩码标志位, 掩码/长度
//        boolean isMasked = (buffer[1] & 0x80) != 0;
//        int payloadLength = buffer[1] & 0x7F;
//        // 如果payloadLength为126或127，需要额外读取长度字段
//        int index = 2;
//        if (payloadLength == 126)
//        {
//            if (bytesRead < 4)
//            {
//                return false; // 长度数据不完整
//            }
//            payloadLength = ((buffer[index] & 0xFF) << 8) | (buffer[index + 1] & 0xFF);
//            index += 2;
//        }
//        else if (payloadLength == 127)
//        {
//            // 长度字段为8字节，这里简化处理，假设不会用到这么大的消息
//            return false;
//        }
//        // 读取掩码键（如果存在）
//        byte[] maskingKey = new byte[4];
//        if (isMasked)
//        {
//            if (bytesRead < index + 4)
//            {
//                return false; // 掩码键数据不完整
//            }
//            System.arraycopy(buffer, index, maskingKey, 0, 4);
//            index += 4;
//        }
//        // 读取有效载荷数据
//        byte[] payloadData = new byte[payloadLength];
//        if (bytesRead < index + payloadLength)
//        {
//            return false; // 有效载荷数据不完整
//        }
//        System.arraycopy(buffer, index, payloadData, 0, payloadLength);
//        // 解码（如果需要）
//        if (isMasked)
//        {
//            for (int i = 0; i < payloadLength; i++)
//            {
//                payloadData[i] ^= maskingKey[i % 4];
//            }
//        }
//        // 转换为字符串（如果是文本帧）
//        if (isTextFrame)
//        {
//            String message = new String(payloadData, StandardCharsets.UTF_8);
//            System.out.println("Received message: " + message);
//        }
//        return true;
//        // 处理其他类型的帧（如关闭帧、二进制帧等）

        if (bytesRead < 2)
        {
            return null; // 数据不完整，返回null
        }

        boolean isFinalFrame = (buffer[0] & 0x80) != 0;
        int opcode = buffer[0] & 0x0F;
        boolean isMasked = (buffer[1] & 0x80) != 0;
        int payloadLength = buffer[1] & 0x7F;
        int index = 2;

        // 处理扩展长度
        if (payloadLength == 126)
        {
            if (bytesRead < index + 2)
            {
                return null; // 长度数据不完整
            }
            payloadLength = ((buffer[index] & 0xFF) << 8) | (buffer[index + 1] & 0xFF);
            index += 2;
        }
        else if (payloadLength == 127)
        {
            // 长度字段为8字节，这里简化处理，假设不会用到这么大的消息
            return null;
        }

        // 处理掩码
        byte[] maskingKey = new byte[4];
        if (isMasked)
        {
            if (bytesRead < index + 4)
            {
                return null; // 掩码数据不完整
            }
            System.arraycopy(buffer, index, maskingKey, 0, 4);
            index += 4;
        }

        // 处理有效载荷数据
        byte[] payloadData = new byte[payloadLength];
        if (bytesRead < index + payloadLength)
        {
            return null; // 有效载荷数据不完整
        }
        System.arraycopy(buffer, index, payloadData, 0, payloadLength);

        // 解码（如果需要）
        if (isMasked)
        {
            for (int i = 0; i < payloadLength; i++)
            {
                payloadData[i] ^= maskingKey[i % 4];
            }
        }
        // 根据操作码处理不同类型的帧
        switch (opcode)
        {
        case 0x01: // 文本帧
            String message = new String(payloadData, StandardCharsets.UTF_8);
            return message;
        case 0x08: // 关闭帧
            webScoketMannger.close();    // 调用close方法关闭连接
            return null;
        // 可以在这里添加其他操作码的处理
        default:
            // 未知的操作码，可以记录日志或抛出异常
            break;
        }

        // 如果不是文本帧或关闭帧，返回null
        return null;
    }
}
