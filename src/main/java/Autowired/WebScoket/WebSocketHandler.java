package Autowired.WebScoket;

public interface WebSocketHandler
{
    default long getMaxOutTime()
    {
        return 1000;
    }
    default int getPort()
    {
        return 8081;
    }
    default int getThreadPool()
    {
        return 10;
    }
    void onOpen(WebScoketMannger webScoketMannger);

    void onClose(WebScoketMannger webScoketMannger);

    void onError(WebScoketMannger webScoketMannger);

    void onMessage(WebScoketMannger webScoketMannger, String message);

    void onCreated(WebScoketMannger webScoketMannger, String id);
}
