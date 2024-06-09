package TestWhileError;

import Autowired.Tools.AppInitializer;

public class App
{
    //测试循环注入， 这里因为最开始受到了springboot的循环注入报错问题，我其实通过分层注入，可以解决循环注入的问题，最后还是能注入成功。
    public static void main(String[] args)
    {
        AppInitializer.launch(App.class);
    }
}
