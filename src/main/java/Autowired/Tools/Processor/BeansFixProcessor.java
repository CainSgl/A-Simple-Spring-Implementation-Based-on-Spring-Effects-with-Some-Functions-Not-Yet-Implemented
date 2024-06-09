package Autowired.Tools.Processor;

import Autowired.BeanGetter.BeanGetter;
import Autowired.BeanGetter.BeanGetterProtoImp;
import Autowired.BeanGetter.BeanGetterSingleImp;
import Autowired.BeanGetter.FuncInterHandler;
import Autowired.BeanGetter.ProxyFactor.ProxyFactor;
import Autowired.Cainsgl.BeanRuntimeException;
import Autowired.Cainsgl.ScopeMode;
import Autowired.Tools.AppInitializer;
import Autowired.Tools.BeansManager;

import Autowired.Tools.Util.IO.MultipartResolver;
import Autowired.Tools.checker.getConfigures;
import Autowired.WebScoket.*;


import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

import static Autowired.Tools.BeansManager.BEGETTER_POOL;

public class BeansFixProcessor
{
    private ClassLoader SystemClassLoader;
    public BeanPostProcessor beanPostProcessor;
    boolean hasWebSocket = false;
    public void ContainerInitializer(Class<?> launchClass, BeanPostProcessor beanPostProcessor) throws BeanRuntimeException
    {

        AppInitializer.beansProcessor.beanPostProcessor = beanPostProcessor;
        ClassLoader classLoader = launchClass.getClassLoader();//App

        SystemClassLoader = classLoader;
        PathProcessor.SetBootStrapDirectory(PathProcessor.getPackageName(launchClass.getTypeName()).replace(".", "/"));
        URL url = classLoader.getResource(PathProcessor.getBootStrapDirectory());
        if (url != null)
        {
            BeansManager.InfoLog.startInjection(url);
            PathProcessor.SetBootStrapDirectory(PathProcessor.getBootStrapDirectory().replace("/", "\\"));
            File AllBeanFile = new File(url.getFile());
            if (AllBeanFile.isDirectory())
            {
                PathProcessor.InitBoot(AllBeanFile);
                getAllBeans(AllBeanFile, AppInitializer.beansProcessor::AddBean);
                BeansManager.InfoLog.startCachPooLInjection();
                AppInitializer.beansManager.LazyCachWired();
                //这里是开启了Proxy的总写入
                AppInitializer.proxyFactor.SetFileForProxy();
                System.out.println("\n\n--------Welcome use AutoWired--------\nCode by Cainsgl           Version:1.9\n-------------------------------------");
                AppInitializer.serverInitializer.InitializeServer();
                if(hasWebSocket)
                {
                    MessageProcess.InitializeSocket();
                }
            }
        }
        else
        {
            AppInitializer.beansManager.BeanError("传入的类的路径出现问题");
        }
    }

    //    private static final Map<String, BeanGetter> beans = new ConcurrentHashMap<>();
    public void AddBean(String ClassName, Object... WritingObject)
    {
        if (ClassName == null)
            return;
        Class<?> clazz;
        try
        {
            clazz = SystemClassLoader.loadClass(ClassName);
        }
        catch (ClassNotFoundException e)
        {
            System.err.println(ClassName + "加载错误\n加载器:" + SystemClassLoader);
            return;
        }
        if (clazz.isAnnotationPresent(Autowired.Cainsgl.annotations.Component.class))
        {
            Autowired.Cainsgl.annotations.Component componentAnnotation = clazz.getAnnotation(Autowired.Cainsgl.annotations.Component.class);
            BeanGetter beanGetter = newInstanceBeanGetter(componentAnnotation, clazz, ClassName, WritingObject);
            if (beanGetter == null)
                return;
            BEGETTER_POOL.put(ClassName, beanGetter);
        }
        else if (clazz.isAnnotationPresent(Autowired.Cainsgl.annotations.Config.class))
        {
            //设置文件
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces)
            {
                if (inter.equals(getConfigures.class))
                {
                    try
                    {
                        Constructor<?> constructor = clazz.getConstructor();
                        constructor.setAccessible(true);
                        getConfigures instance = (getConfigures) constructor.newInstance();
                        AppInitializer.serverInitializer.port = instance.getPort();
                        MultipartResolver.DEFAULT_BUFFER_SIZE = instance.getBufferSize();
                        MultipartResolver.Max_File_Size = instance.getMaxFileSize();
                        //TODO 设置mybatis的密码
                        System.out.println("还没有做动态修改mybatis的密码和账号，以及使用类在加载器动态配置");
                        AppInitializer.getConfigures = instance;
                        AppInitializer.dependencechecker.CheckDependence();
                        //TODO
                    }
                    catch (Exception e)
                    {
                        AppInitializer.beansManager.AutoWiredError("Error:Config注解的类当中，无法生成对应的实例", clazz);
                        throw new BeanRuntimeException("无法生成对应的实例，请查看自己是否提供了无参构造器", 0);
                    }
                    return;
                }
            }
            AppInitializer.beansManager.AutoWiredError("Error:你的Config注解出现在了一个错误的类上面", clazz);
            throw new BeanRuntimeException("你的Config注解类未实现getConfigures接口", 0);
        }
        if (clazz.isAnnotationPresent(ServerWebSocketHandler.class))
        {
            ServerWebSocketHandler endpointAnnotation = clazz.getAnnotation(ServerWebSocketHandler.class);

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> i : interfaces)
            {
                if (i == WebSocketHandler.class)
                {
                    try{

                        if(endpointAnnotation.path()==null)
                        {
                            throw new BeanRuntimeException("请不要设置Path为空",1);
                        }
                        WebScoketHolder.ServerPath=endpointAnnotation.path();
                        Constructor<?> constructor = clazz.getConstructor();
                        constructor.setAccessible(true);
                        WebSocketHandler instance = (WebSocketHandler) constructor.newInstance();
                        WebScoketMannger.WebSocketHandler=instance;

                        MessageProcess.port=instance.getPort();
                        MessageProcess.ThreadPoolSize=instance.getThreadPool();
                        MessageProcess.maxOutTime=instance.getMaxOutTime();
                        hasWebSocket=true;

                    }catch (Exception e)
                    {
                        AppInitializer.beansManager.AutoWiredError("Error:ServerEndpoint注解的类当中，无法生成对应的实例", clazz);
                        throw new BeanRuntimeException("无法生成对应的实例，请查看自己是否提供了无参构造器", 0);
                    }
                    return;
                }
            }
            AppInitializer.beansManager.AutoWiredError("Error:你的@ServerEndpoint注解出现在了一个错误的类上面", clazz);
            throw new BeanRuntimeException("@ServerEndpoint只能使用在实现了WebSocketHandler的类上", 2);
        }
    }

    private BeanGetter newInstanceBeanGetter(Autowired.Cainsgl.annotations.Component componentAnnotation, Class<?> clazz, String className, Object... WritingObject)
    {
//        System.out.println("实例化Bean"+className);
        if (BEGETTER_POOL.containsKey(className))
        {
            //说明是在构造其他单例的时候，需要提前创建，已经提前写入了。
//            System.out.println("池中已经有了"+className);
            return null;
        }


        BeanGetter beanGetter = null;
        if (clazz.isInterface())
        {
            AppInitializer.beansManager.AutoWiredError("Bean类型对象不能是接口", clazz);
        }
        try
        {
            if (WritingObject == null || WritingObject.length == 0)
            {
                if (componentAnnotation.Scope() == ScopeMode.Singleton)
                {
//                    System.out.println("一个参数构造器启动"+ReAutoWired);
                    beanGetter = new BeanGetterSingleImp(clazz.getConstructor());
                }
                else
                {
//                    System.out.println("一个参数构造器启动"+ReAutoWired);
                    beanGetter = new BeanGetterProtoImp(clazz.getConstructor());
                }
            }
            else
            {
                if (componentAnnotation.Scope() == ScopeMode.Singleton)
                {
//                    System.out.println("多个参数构造器"+ReAutoWired);

                    beanGetter = new BeanGetterSingleImp(clazz.getConstructor(), WritingObject);
                }
                else
                {
//                    System.out.println("多个参数构造器"+ReAutoWired);
                    beanGetter = new BeanGetterProtoImp(clazz.getConstructor(), WritingObject);
                }
            }
        }
        catch (NoSuchMethodException e)
        {
            //没有对应的构造器
            AppInitializer.beansManager.AutoWiredError("Bean类型没有无参构造器", clazz);
        }
        catch (ReflectiveOperationException e)
        {
            AppInitializer.beansManager.AutoWiredError("Bean类型对象的构造器无法生成正常实例对象", clazz);
            //构造器生成实例错误
        }
        return beanGetter;
    }


    public static Object GetBean(String ClassName) throws Exception
    {
        BeanGetter beanGetter = BEGETTER_POOL.get(ClassName);

        if (beanGetter == null)
        {
            System.err.println(
                    "Autowired.Cainsgl.BeanException\t" + "找不到你要的Bean对象" + "\n" +
                            "\tat " + ClassName + "(" + ClassName + ".java:0)"
            );
            throw new BeanRuntimeException("这里错误的调用", 1);
        }
        if (ProxyFactor.BeanGetterProxy.containsKey(beanGetter))
        {
            //说明要拿的是代理对象
            return ProxyFactor.BeanGetterProxy.get(beanGetter);
        }
        return beanGetter.getBean();
    }

    public static <T> T GetBean(Class<T> clazz) throws Exception
    {
        return (T) GetBean(clazz.getTypeName());
    }

    private void getAllBeans(File Directory, FuncInterHandler<String> funcInterHandler)
    {
        File[] files = Directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                getAllBeans(file, funcInterHandler);
            }
            else
            {
                String beanAbName = file.getAbsolutePath();
                funcInterHandler.Handler(PathProcessor.getPackageNameByABPath(beanAbName));
            }
        }
    }
}
