package Test.beans;

import Autowired.WebScoket.ServerWebSocketHandler;
import Autowired.WebScoket.WebScoketMannger;
import Autowired.WebScoket.WebSocketHandler;
@ServerWebSocketHandler(path ="test")
public class WebScoket implements WebSocketHandler
{
    @Override
    public long getMaxOutTime()
    {
        return WebSocketHandler.super.getMaxOutTime();
    }

    @Override
    public int getPort()
    {
        return 9090;
    }

    @Override
    public int getThreadPool()
    {
        return WebSocketHandler.super.getThreadPool();
    }

    @Override
    public void onOpen(WebScoketMannger webScoketMannger)
    {
        System.out.println( "open"+ webScoketMannger);
    }

    @Override
    public void onClose(WebScoketMannger webScoketMannger)
    {
        System.out.println( "close"+ webScoketMannger);
    }

    @Override
    public void onError(WebScoketMannger webScoketMannger)
    {
        System.out.println( "error"+ webScoketMannger);
    }

    @Override
    public void onMessage(WebScoketMannger webScoketMannger, String message)
    {
        System.out.println("messgaege"+message);
    }

    @Override
    public void onCreated(WebScoketMannger webScoketMannger, String id)
    {
        System.out.println("created"+webScoketMannger);
    }
}
