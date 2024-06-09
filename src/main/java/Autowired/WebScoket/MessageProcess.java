package Autowired.WebScoket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcess
{
    public static ServerSocket serverSocket;
    public static ExecutorService threadPool;
    public static long maxOutTime = 1000;
    public static int port=8081;
    public static int ThreadPoolSize=10;
    public static void InitializeSocket()
    {
        try
        {
            serverSocket = new ServerSocket(port);
            threadPool = Executors.newFixedThreadPool(ThreadPoolSize);
            System.out.println(
                               "\n--------Welcome use WebSocket--------" +
                    "\nCode by Cainsgl           Version:1.0" +
                    "\nPooLSize:"+ThreadPoolSize +"\nPort:"+port+"\nPath:"+WebScoketHolder.ServerPath+
                    "\n-------------------------------------");
            threadPool.execute(MessageProcess::GetClientSocket);
        }
        catch (Exception e)
        {
            System.err.println("启动失败");
        }
    }

    static void GetClientSocket()
    {
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                new SocketProceeder(socket);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class SocketProceeder
    {
        String address;
        BufferedReader reader;

        public SocketProceeder(Socket socket) throws IOException
        {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            address = socket.getRemoteSocketAddress().toString();
            threadPool.execute( ()->new WebScoketHolder(reader, address, socket));
        }
    }
}
