package Autowired.Tools;

import Autowired.BeanGetter.ProxyFactor.ProxyFactor;
import Autowired.Cainsgl.BeanRuntimeException;
import Autowired.Tools.InfoLog.PrintHttpInfo;
import Autowired.Tools.InfoLog.PrintInjectInfoExecutor;
import Autowired.Tools.Processor.BeanPostProcessor;
import Autowired.Tools.Processor.BeansFixProcessor;

import Autowired.Tools.TomcatServer.ServerInitializer;
import Autowired.Tools.checker.Dependencechecker;
import Autowired.Tools.checker.getConfigures;


public class AppInitializer
{
    private AppInitializer()
    {

    }
    public static Dependencechecker dependencechecker=null;
    public static BeansFixProcessor beansProcessor=null;
    private static boolean isLaunched = false;
    public static BeansManager beansManager=null;
    public static ServerInitializer serverInitializer=null;
    public static ProxyFactor proxyFactor=null;//生成代理对象
    public static getConfigures getConfigures=null;
    /**
     * <h3>自动注入</h3>
     * <p>传入一个Class对象即可，一般为启动类的，他会自动搜索</p>
     *<a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @param launchClass Class 这个Class的同一个包或子包下的类会被初始化并实例化装入Bean
     * @author Cainsgl
     **/
    public static void launch(Class<?> launchClass)
    {
       launch(launchClass, new BeanPostProcessor() {});
    }
    /**
     * <h3>自动注入</h3>
     * <p>传入一个Class对象即可，一般为启动类的，他会自动搜索</p>
     *<a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @param launchClass Class 这个Class的同一个包或子包下的类会被初始化并实例化装入Bean
     * @param beanPostProcessor 在bean初始化前后会调用他,并且替换原来的bean
     * @author Cainsgl
     **/
    public static void launch(Class<?> launchClass, BeanPostProcessor beanPostProcessor) throws BeanRuntimeException
    {
        if(isLaunched)
        {
            throw new BeanRuntimeException("重复调用了lauch方法，lauch只被调用一次",1);
        }
        if( BeansManager.InfoLog==null)
            BeansManager.InfoLog=new PrintInjectInfoExecutor() {};
        dependencechecker=new Dependencechecker();
        beansProcessor=new BeansFixProcessor();
        beansManager=new BeansManager();
        serverInitializer=new ServerInitializer();
        proxyFactor=new ProxyFactor();
        beansProcessor.ContainerInitializer(launchClass,beanPostProcessor);
        dependencechecker.WiredAllMapper();

        isLaunched=true;
    }
    /**
      * @param printInjectInfoExecutor 这个是打印信息，注入后的信息，你可以手动继承他
     **/
    public static void SetPrintInjectInfoExecutor(PrintInjectInfoExecutor printInjectInfoExecutor)
    {
        BeansManager.InfoLog=printInjectInfoExecutor;
    }

    /**
     * <h3>获取Bean</h3>
     * <p>传入类对象即可，如果没有对应的Component注解就会报错</p>
     * @param Class Class 你需要的实例，该类必须使用Component注解
     *<a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    public static <T>T getBean(Class<T> Class) throws Exception
    {
        return  BeansFixProcessor.GetBean(Class);
    }
    /**
     * <h3>获取Bean</h3>
     * <p>传入类对象的全限名即可，如果没有对应的Component注解就会报错</p>
     * @param ClassName String 你需要的实例的全限名，该类必须使用Component注解
     *<a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
     * @author Cainsgl
     **/
    public static Object getBean(String ClassName)throws Exception
    {
        return  BeansFixProcessor.GetBean(ClassName);
    }
    public static PrintHttpInfo printHttpInfo=new PrintHttpInfo() {};
    public static void SetHttpInfo(PrintHttpInfo printHttpInfoLog)
    {
        printHttpInfo=printHttpInfoLog;
    }

    public static void destructor()
    {
        if(!isLaunched)
        {
            throw new BeanRuntimeException("请先调用launch方法",1);
        }
        proxyFactor=null;
        beansManager=null;
        beansProcessor=null;
        serverInitializer=null;
        dependencechecker=null;
    }
}
