package Autowired.Tools.InfoLog;

import java.lang.reflect.Field;
import java.net.URL;

public interface PrintInjectInfoExecutor
{
    default void directInjectionInfo(Object needWiredObject, Field f)
    {
        System.out.println("\t\tInjection success:直接注入 by Cainsgl:\t" + needWiredObject.getClass().getName() + "." + f.getName());
    }

    default void LazyInjectionInfoSucess(Object needWiredObject, Field f)
    {
        System.out.println("\t\tInjection success:惰性注入 by Cainsgl:\t" + needWiredObject.getClass().getName() + "." + f.getName());
    }

    default void LazyInjectionInfoAwait(Object needWiredObject, Field f)
    {
        System.out.println("\t\tInjection await  :惰性注入 by Cainsgl:\t" + needWiredObject.getClass().getName() + "." + f.getName());
    }

    default void startCachPooLInjection()
    {
        System.out.println("\t\t启动缓冲池注入");
    }

    default void startInjection(URL url)
    {
        System.out.println("启动软件包（该软件包或及其子软件包下的所有Component注解的类皆会被实例化）：" + url.getPath());
    }
}
