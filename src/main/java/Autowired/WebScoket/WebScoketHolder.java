package Autowired.WebScoket;


import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebScoketHolder
{
    BufferedReader reader;
    String address;
    public static String ServerPath;
    public WebScoketHolder(BufferedReader reader, String address, Socket scoket)
    {
        String path=null;
        String line = null;
        int n = 0;
        this.address = address;
        this.reader = reader;
        Map<String, String> header = new ConcurrentHashMap<String, String>();
        try
        {
            long now;
            int lineNum = 0;
            boolean breakall = false;
            while (true)
            {
                now = System.currentTimeMillis();
                while (reader.ready())
                {
                    lineNum++;
                    line = reader.readLine();
                    if (line.isEmpty())
                    {
                        ResponseSucess(scoket, header);
                        return;
                    }
                    if (lineNum == 1)
                    {
                        if (line.startsWith("GET"))
                        {
                            int i = line.lastIndexOf("HTTP");
                            if (i != -1)
                            {
                                path = line.substring(5, i-1);
                                if(!path.equals(ServerPath))
                                {
                                    breakall=true;
                                    break;
                                }
                            }
                            else
                            {
                                //不是websocket
                                breakall = true;
                                break;
                            }
                        }
                        else
                        {
                            //不是web
                            breakall = true;
                            break;
                        }
                    }
                    else
                    {
                        int i = line.indexOf(":") + 1;
                        if (i != 0)
                        {
                            header.put(line.substring(0, i - 1), line.substring(i + 1));
                        }
                        else
                        {
                            //错误的请求格式
                            breakall = true;
                            break;
                        }
                    }
                }
                if (breakall)
                {
                    System.err.println(address + "发送的不是正确的请求格式,解析失败.或者是不受支持的路径:=>"+path);
                    System.out.println("支持的路径:=>"+ServerPath);
                    break;
                }

                while (System.currentTimeMillis() - now < MessageProcess.maxOutTime / 10)
                    ;//自旋
                n++;
                if (n > 10)
                {
                    scoket.close();
                    System.err.println(address + ":连接超时");
                    break;
                }
            }
            if (breakall)
            {
                System.err.println(address + "发送了一个错误的请求格式，已成功关闭管道");
                scoket.close();
            }
        }
        catch (Exception e)
        {
            System.err.println(address + "建立失败:原因,无法读取到里面的数据");
            try{
                scoket.close();
            }catch (Exception e2){
                System.err.println(scoket+"关闭失败，无法关闭？");
            }
        }
    }

    public void ResponseSucess(Socket scoket, Map<String, String> header)
    {
        try
        {
            String clientKey = header.get("Sec-WebSocket-Key");
            if (clientKey == null)
            {
                System.out.println(address + "客户端发送了一个错误的请求，他并不是握手协议");
                scoket.close();
                return;
            }
            String serverKey = generateWebSocketAccept(clientKey);
            OutputStream outputStream = scoket.getOutputStream();
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: " + serverKey + "\r\n" +
                    "\r\n";
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            WebScoketMannger webScoketMannger = new WebScoketMannger(serverKey, scoket, outputStream);
            System.out.println(address + "已成功建立WebSocket");
            WebScoketMannger.WebSocketHandler.onOpen(webScoketMannger);
            System.out.println("返回的Key:=>" + serverKey);
            webScoketMannger.run();
        }
        catch (Exception e)
        {
            System.err.println(address + "获取输出流对象失败");
        }
    }

    public static String generateWebSocketAccept(String clientKey)
    {
        try
        {
            String magicGUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            String input = clientKey + magicGUID;
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.err.println("生成ServerKey失败");
        }
        return null;
    }
}
