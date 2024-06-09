package Autowired.Tools;

import Autowired.BeanGetter.ProxyFactor.ProxyFactor;
import Autowired.Cainsgl.annotations.Aop.*;
import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.BeanException;
import Autowired.BeanGetter.BeanGetter;
import Autowired.Cainsgl.BeanRuntimeException;
import Autowired.Cainsgl.annotations.Lazy;
import Autowired.Cainsgl.annotations.request.GetMapping;
import Autowired.Cainsgl.annotations.request.PostMapping;
import Autowired.Tools.InfoLog.PrintInjectInfoExecutor;
import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeansManager
{
    public static final Map<String, BeanGetter> BEGETTER_POOL = new ConcurrentHashMap<>();


    public static final Map<Field, Object> LAZYOBJECT_CACH = new ConcurrentHashMap<>();

    //这里只有查询是多线程
    public static final Map<String, Method> Get_URL_CALL = new HashMap<>();
    public static final Map<String, Method> Post_URL_CALL = new HashMap<>();

    public static final Map<Method, Object> METHOD_HOLDERS = new HashMap<>();


    public static PrintInjectInfoExecutor InfoLog = null;


    private void getURLmethod(Object bean)
    {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods)
        {
            AppInitializer.proxyFactor.getMetHods().put(bean.getClass().getName() + "." + method.getName(), method);
            ProxyFactor.MethodProxy.put(method, bean);
            AopMethodSet(method);
            METHOD_HOLDERS.put(method, bean);
            if (method.isAnnotationPresent(GetMapping.class))
            {
                Get_URL_CALL.put(method.getAnnotation(GetMapping.class).value(), method);
            }
            else if (method.isAnnotationPresent(PostMapping.class))
            {
                Post_URL_CALL.put(method.getAnnotation(PostMapping.class).value(), method);
            }
        }
    }

    public void AopMethodSet(Method method)
    {
        if (method.isAnnotationPresent(After.class))
        {
            After after = method.getAnnotation(After.class);
            AppInitializer.proxyFactor.AfterMethods.put(method, after.value());
        }
        if (method.isAnnotationPresent(AfterReturn.class))
        {
            AfterReturn afterReturn = method.getAnnotation(AfterReturn.class);
            AppInitializer.proxyFactor.AfterMethodRetruns.put(method, afterReturn.value());
        }
        if (method.isAnnotationPresent(AfterThrow.class))
        {
            AfterThrow afterThrow = method.getAnnotation(AfterThrow.class);
            AppInitializer.proxyFactor.AfterThrowingMethods.put(method, afterThrow.value());
        }
        if (method.isAnnotationPresent(Before.class))
        {
            Before before = method.getAnnotation(Before.class);
            AppInitializer.proxyFactor.BeforeMethods.put(method, before.value());
        }
    }


    public void LazyCachWired()
    {
        for (Map.Entry<Field, Object> entry : LAZYOBJECT_CACH.entrySet())
        {
            Field f = entry.getKey();
            Object o = entry.getValue();
            InfoLog.LazyInjectionInfoSucess(o, f);
            SetAllFieldValue(o, f, o);
        }
    }


    public void AutoWiredError(String msg, Class<?> clazz)
    {
        System.err.println(
                "Autowired.Cainsgl.BeanException\t" + msg + "\n" +
                        "\tat " + clazz.getName() + "(" + clazz.getSimpleName() + ".java:0)"
        );
    }


    public void BeanError(String msg)
    {
        try
        {
            throw new BeanException(msg);
        }
        catch (BeanException e)
        {
            System.err.println(e.getMessage());
            StackTraceElement[] stackTrace = e.getStackTrace();
            int len = stackTrace.length - 1;
            for (int i = len; i > 1; i--)
            {
                System.err.println(stackTrace[i]);
            }
        }
    }

    public void InjectBean(Object needWiredObject, Object... writingObject)
    {
        needWiredObject = AppInitializer.beansProcessor.beanPostProcessor.postProcessBeforeInitialization(needWiredObject, needWiredObject.getClass().getSimpleName());
        //获取里面的方法，get,post
        getURLmethod(needWiredObject);
        //开始注入里面的值（注入前应该先检测里面注入的对象里面的值是否也需要注入，不然的先注入）
        Field[] fields = needWiredObject.getClass().getDeclaredFields();
        for (Field f : fields)
        {
            if (f.isAnnotationPresent(Autowired.class))
            {
                if (f.getType().isAnnotationPresent(Mapper.class))
                {
                    AppInitializer.dependencechecker.MapperWillAutoWired.put(f, needWiredObject);
                }
                else
                {
                    if (f.isAnnotationPresent(Lazy.class))
                    {
                        //放入缓冲池，下一次再注入
                        InfoLog.LazyInjectionInfoAwait(needWiredObject, f);
                        LAZYOBJECT_CACH.put(f, needWiredObject);
                    }
                    else
                    {
                        InfoLog.directInjectionInfo(needWiredObject, f);
                        SetAllFieldValue(needWiredObject, f, writingObject);
                    }
                }
            }
        }
    }

    private void SetAllFieldValue(Object needWiredObject, Field f, Object... writingObject)
    {
        //开始写入，先检测这个类里面的Fields里有没有也要
        if (BEGETTER_POOL.get(f.getType().getTypeName()) != null)
        {
            SetFieldValue(f, needWiredObject);
        }
        else
        {
            for (Object o : writingObject)//检验是否是循环注入
            {
                //这里其实可以解决，不过最开始采取的是spring，spring循环注入会报错，其实这里我分层注入，解决了这个问题，循环也没事,也可以正确注入
                if (f.getType().equals(o.getClass()))
                {
                    AppInitializer.beansManager.AutoWiredError("循环注入，某一容器里需要自动注入的对象包含了该容器的字段或其他", f.getType());
                    throw new BeanRuntimeException("循环注入", 1);
                }
            }

            if (f.getType().isAnnotationPresent(Mapper.class))
            {
                return;
            }

//池中没有对应的实例，说明这个实例是在后面加入的，但是我现在需要用，需要提前创建。
            AppInitializer.beansProcessor.AddBean(f.getType().getTypeName(), writingObject); //最终又会调自己，这里其实是递归,不过递归writingObject会不断的增加，正在注入的,两层递归
            if (BEGETTER_POOL.get(f.getType().getTypeName()) != null)
            {
                SetFieldValue(f, needWiredObject);
            }
            else
            {
                throw new BeanRuntimeException("在最后的注入当中，仍然无法拿到对应的BeanGetter\n\t" + needWiredObject.getClass().getTypeName() + "." + f.getName(), 1);
            }
        }
    }

    public void SetFieldValue(Field f, Object needWiredObject)
    {
        needWiredObject = AppInitializer.beansProcessor.beanPostProcessor.postProcessAfterInitialization(needWiredObject, f.getName());
        AppInitializer.proxyFactor.putProxy(f, needWiredObject);
    }
}
