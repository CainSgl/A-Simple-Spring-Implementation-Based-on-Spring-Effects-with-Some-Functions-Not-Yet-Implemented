package Autowired.BeanGetter.ProxyFactor;

import Autowired.BeanGetter.BeanGetter;
import Autowired.Cainsgl.BeanRuntimeException;
import Autowired.Cainsgl.ScopeMode;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;
import Autowired.Tools.AppInitializer;
import Autowired.Tools.BeansManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyFactor
{
    public ProxyFactor()
    {
        AllbeansForAutoWired = new ConcurrentHashMap<Field, Object>();
        MetHods = new HashMap<>();
        AfterMethods = new HashMap<>();
        BeforeMethods = new HashMap<>();
        AfterMethodRetruns = new HashMap<>();
        AfterThrowingMethods = new HashMap<>();
    }


    Map<Field, Object> AllbeansForAutoWired = null;

    public Map<String, Method> getMetHods()
    {
        return MetHods;
    }

    public void putProxy(Field f, Object bean)//等缓冲池输入结束再生成动态对象
    {
        AllbeansForAutoWired.put(f, bean);
    }

    public void SetFileForProxy()
    {
        //这里开始真正的写值进去
        //回调的时候使用，会写入所有的的@AutoWired
        for (Map.Entry<Field, Object> entry : AllbeansForAutoWired.entrySet())
        {
            Field f = entry.getKey();
            Object o = entry.getValue();
            f.setAccessible(true);
            try
            {
                //这里是生成动态代理对象
                EnableProxy enableProxy = f.getType().getAnnotation(EnableProxy.class);
                Component component = f.getType().getAnnotation(Component.class);
                String PoolKey = null;
                if (component == null)
                {
                    //BeanGetter
                    enableProxy = f.get(o).getClass().getAnnotation(EnableProxy.class);
                    component = f.get(o).getClass().getAnnotation(Component.class);
                    PoolKey = f.get(o).getClass().getTypeName();
                }
                if (enableProxy != null && component.Scope() == ScopeMode.Prototype)
                {
                    AppInitializer.beansManager.AutoWiredError("这里enableProxy注解代理的对象不允许是原型(Prototype)", f.getType());
                    throw new BeanRuntimeException("enableProxy注解出现在了不允许出现的地方", 3);
                }
                if (component.Scope() == ScopeMode.Prototype || enableProxy == null)
                {
                    if (PoolKey == null)
                        f.set(o, BeansManager.BEGETTER_POOL.get(f.getType().getTypeName()).getBean());
                    else
                        f.set(o, BeansManager.BEGETTER_POOL.get(PoolKey).getBean());
                }
                else if (component.Scope() == ScopeMode.Singleton)
                {
                    BeanGetter beanGetter;
                    if (PoolKey == null)
                        beanGetter = BeansManager.BEGETTER_POOL.get(f.getType().getTypeName());
                    else
                        beanGetter = BeansManager.BEGETTER_POOL.get(PoolKey);
                    getProxy(beanGetter, o, f);
                    //这里是删除原先的方法对bean，换成代理对象

//                        BeanGetter beanGetter = BeansManager.BEGETTER_POOL.get(f.getType().getTypeName());
//                        if (beanToProxy.containsKey(beanGetter))
//                        {
//                            f.set(o,beanGetter.getBean() );
//                        }
//                        else
//                        {
//                            Object bean = beanGetter.getBean();
//                            //没用配置Proxy
//                            Object proxy = getProxy(bean, beanGetter);
//                            f.set(o, bean);
                    //                   }

//
//
//                            //从AllbeansForAutoWired删除
//                            int j = -1;
//                            for (Map.Entry<Field, Object> entry2 : AllbeansForAutoWired.entrySet())
//                            {
//                                j++;
//                                Field f2 = entry2.getKey();
//                                Object o2 = entry2.getValue();
//                                if (o2 == bean)
//                                {
//                                    if (j > i)
//                                    {
//                                        //不影响,删除后，等后续的遍历再注入
//                                        AllbeansForAutoWired.remove(f2);
//                                        i--;
//                                    }
//                                    else
//                                    {
//                                        //影响，删除后，后续会重写遍历到他,原先哪个bean已经被写入了一部分
//                                    }
//                                    System.out.println(f2);
//                                    System.out.println("放入的"+ proxyFields[len]+"\tproxy\t"+proxy);
//                                    AllbeansForAutoWired.put(proxyFields[len], proxy);
//                                    len++;
//                                }
//                            }


//                    {
//                        Object bean = beanGetter.getBean();
//                        Object proxy = getProxy(beanGetter.getBean(), beanGetter);
//                        // 对proxy进行注入
//                        //修改AllbeansForAutoWired里面的值
//                        Map<Field, Object> FiledCach = new ConcurrentHashMap<Field, Object>();
//                        //因为bean是原型注入，所有写里面的值的时候都写进这个proxy;
//                        Field[] fields = bean.getClass().getDeclaredFields();
//                        for (Field field : fields)
//                        {
//                            if (field.isAnnotationPresent(Autowired.class))
//                                FiledCach.put(field, bean);
//                        }
//                        for (Map.Entry<Field, Object> WiredEntry : AllbeansForAutoWired.entrySet())
//                        {
//                            Field f2 = WiredEntry.getKey();
//                            if (FiledCach.containsKey(f2))
//                            {
//                                AllbeansForAutoWired.remove(f2);
//                            }
//                        }
//                        Field[] fieldsProxy = bean.getClass().getDeclaredFields();
//                        for (Field fProxy : fieldsProxy)
//                        {
//                            if (fProxy.isAnnotationPresent(Autowired.class))
//                                AllbeansForAutoWired.put(fProxy, proxy);
//                        }
//                        f.set(o, proxy);
//
                }
            }
            catch (IllegalAccessException e)
            {
                System.err.println("在对需要注入的字段里的对应类里面也有需要注入的字段，但是在注入里面的字段的时候发送了不可预料的错误");
                e.printStackTrace();
            }
        }
        copyAllBeean();
        AllbeansForAutoWired.clear();//清空
    }

    //他们的String可以是正则表达式
    public Map<Method, String> BeforeMethods = null;//try{ call   方法 }
    public Map<Method, String> AfterMethodRetruns = null;//try{    方法    }.finlly{ AfterMethodRetrun }
    public Map<Method, String> AfterThrowingMethods = null;//try{    方法    }.catch{call  }
    public Map<Method, String> AfterMethods = null;//try{ 方法 call}
    Map<String, Method> MetHods = null;
    //////////////////////////////////////////
    public Map<Field, Object> WriteOBJTOProxy = new ConcurrentHashMap<>();
    public Map<Field, Object> ReadOBJForNeedWirte = new ConcurrentHashMap<>();
    public Map<Field, Object> BeanObject = new ConcurrentHashMap<>();
    public static Map<BeanGetter, Object> BeanGetterProxy = new ConcurrentHashMap<>();
    public static Map<Object, Object> ObjectProxy = new ConcurrentHashMap<>();
    public static Map<Method, Object> MethodProxy = new ConcurrentHashMap<>();

    public void getProxy(BeanGetter beanGetter, Object needWirte, Field field)
    {
        if (BeanGetterProxy.containsKey(beanGetter))
        {
            return;
        }
        Object bean = beanGetter.getBean();
        //找到这些方法
        Method[] methods = bean.getClass().getMethods();
        Map<Method, Method> befores = new HashMap<>();
        Map<Method, Method> afters = new HashMap<>();
        Map<Method, Method> AfterRetruns = new HashMap<>();
        Map<Method, Method> AfterThrows = new HashMap<>();
        for (Method m : methods)
        {
            String methodName = bean.getClass().getTypeName() + "." + m.getName();
            for (Map.Entry<Method, String> entry : BeforeMethods.entrySet())
            {
                String regx = entry.getValue();
                if (methodName.matches(regx))
                {
//                    System.out.println( "\nBefore方法匹配成功：\n" +
//                            "被匹配的  :=>"+methodName+
//                            "\n" +
//                            "注册在内的:=>"+entry.getKey()+"\n");
                    befores.put(m, entry.getKey());
                }
            }
            for (Map.Entry<Method, String> entry : AfterMethods.entrySet())
            {
                String regx = entry.getValue();
                if (methodName.matches(regx))
                {
//                    System.out.println( "\nAfter方法匹配成功：\n" +
//                            "被匹配的  :=>"+methodName+
//                            "\n" +
//                            "注册在内的:=>"+entry.getKey()+"\n");
                    afters.put(m, entry.getKey());
                }
            }
            for (Map.Entry<Method, String> entry : AfterThrowingMethods.entrySet())
            {
                String regx = entry.getValue();
                if (methodName.matches(regx))
                {
//                    System.out.println( "\nAfterThrowing方法匹配成功：\n" +
//                            "被匹配的  :=>"+methodName+
//                            "\n" +
//                            "注册在内的:=>"+entry.getKey()+"\n");
                    AfterThrows.put(m, entry.getKey());
                }
            }
            for (Map.Entry<Method, String> entry : AfterMethodRetruns.entrySet())
            {
                String regx = entry.getValue();
                if (methodName.matches(regx))
                {
//                    System.out.println( "\nAfterReturn方法匹配成功：\n" +
//                            "被匹配的  :=>"+methodName+
//                            "\n" +
//                            "注册在内的:=>"+entry.getKey()+"\n");
                    AfterRetruns.put(m, entry.getKey());
                }
            }
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        MethodInterceptor methodInterceptor = new MethodInterceptor()
        {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
            {
                Object result = null;
                try
                {
                    CallFunction(befores.get(method), befores, objects);
                    result = methodProxy.invokeSuper(o, objects);
                    CallFunction(afters.get(method), afters, objects);
                }
                catch (Throwable e)
                {
                    CallFunction(AfterThrows.get(method), AfterThrows, objects);
                    System.err.println(
                            "Summer:Error\t执行该方法的时候出错\n" +
                                    "\tat " + o.getClass().getName() + "." + method.getName() + "(" + o.getClass().getSimpleName() + ".java:0)"+"" +
                                    "\n错误信息:=>"+e.getMessage()
                    );
                    e.printStackTrace();
                    return e.getMessage();
                }
                CallFunction(AfterRetruns.get(method), AfterRetruns, objects);
                return result;
            }
        };

        enhancer.setCallback(methodInterceptor);
        Object proxy = enhancer.create();
        if (proxy == null)
        {
            throw new BeanRuntimeException("无法创建" + bean.getClass().getName() + "的代理对象", 1);
        }
        WriteOBJTOProxy.put(field, proxy);
        ReadOBJForNeedWirte.put(field, needWirte);
        BeanObject.put(field, bean);
        BeanGetterProxy.put(beanGetter, proxy);
        ObjectProxy.put(bean, proxy);
    }

    public void CallFunction(Method method, Map<Method, Method> where, Object[] objects)
    {
        if (method == null)
            return;
        Object o = null;
        o = MethodProxy.get(method);
        if (o == null)
        {
            System.err.println("无法调用AOP方法，因为无法找到对应的Bean对象");
            return;
        }
        if (ObjectProxy.containsKey(o))
        {
            o = ObjectProxy.get(o);
        }
        method.setAccessible(true);
        try
        {
            Parameter[] parameters = method.getParameters();
            Object[] params = new Object[parameters.length];
            int len = 0;
            for (int i = 0; i < parameters.length; i++)
            {
                for (int j = 0; j < objects.length; j++)
                {
                    if (parameters[i].getType() == objects[j].getClass())
                    {
                        params[i] = objects[j];
                        len++;
                    }
                }
            }
            if (len < parameters.length)
            {
                System.err.println(
                        "\n方法回调错误：需要的参数列表:=>\t" + method.getName() + toParamsString(parameters) +
                                "\n          :实际的参数列表:=>\t" + method.getName() + toParamsString(objects) +
                                "\n          :装配的参数列表:=>\t" + method.getName() + toParamsString(params) + "\n"
                );
            }
            method.invoke(o, params);
        }
        catch (Exception e)
        {
            System.err.println("执行方法的时候出错：" + method);
            System.err.println(
                    "\n执行AOP代理对象的时候出错：需要的参数列表:=>\t" + method.getName() + toParamsString(method.getParameters()) +
                            "\n                     :得到的参数列表:=>\t" + method.getName() + toParamsString(objects)
            );
            e.printStackTrace();
        }

    }

    public static String toParamsString(Object[] o)
    {
        //打印参数
        if (o == null)
            return "( 空 )";
        StringBuilder sb = new StringBuilder();
        if (o.length == 0)
        {
            sb.append("( )");
            return sb.toString();
        }
        if (o[0] != null)
            sb.append("( ").append(o[0]);
        else
            sb.append("( ").append("空");
        if (o.length != 1)
            sb.append(" , ");
        for (int i = 1; i < o.length - 1; i++)
        {
            if (o[i] != null)
                sb.append(o[i].toString()).append(" , ");
            else
                sb.append("空").append(" , ");
        }
        if (o.length == 1)
        {
            return sb.append(" ) ").toString();
        }
        if (o[o.length - 1] != null)
            sb.append(o[o.length - 1].toString());
        else
            sb.append("空");
        sb.append(" ) ");
        return sb.toString();
    }

    public void copyAllBeean()
    {
        System.out.println("被注册在内的Before方法" + BeforeMethods);
        System.out.println("被注册在内的AfterReturn方法" + AfterMethodRetruns);
        System.out.println("被注册在内的Throw方法" + AfterThrowingMethods);
        System.out.println("被注册在内的After方法" + AfterMethods);

        System.out.println("所有GET方法：" + BeansManager.Get_URL_CALL);
        System.out.println("所有POST方法：" + BeansManager.Post_URL_CALL);
        System.out.println("\n三级缓冲池启动");
        for (Map.Entry<Field, Object> entry : WriteOBJTOProxy.entrySet())
        {
            //真的该死，是哪个傻狗写的文章，还说子类的字段和父类的不是同一个
            // 害我debug de半天，调试窗里的都是一样的
            // cnm
            // 我就说，我早该试，通过父类的字段直接映射修改子类的。浪费了我半天
            //我真tm记住你了
            Field field = entry.getKey();
            Object proxy = entry.getValue();
            Object needWirte = ReadOBJForNeedWirte.get(field);
            Object bean = BeanObject.get(field);
            try
            {
                copyFields(bean, proxy);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            field.setAccessible(true);
            try
            {
                field.set(needWirte, proxy);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void copyFields(Object source, Object target) throws IllegalAccessException
    {
        Field[] fieldssource = source.getClass().getDeclaredFields();
        for (Field f : fieldssource)
        {
            f.setAccessible(true);
            if (f.getType().getAnnotation(EnableProxy.class) != null)
            {
                //说明他是原型注入
                if (ObjectProxy.get(f.get(source)) != null)
                    f.set(target, ObjectProxy.get(f.get(source)));
                else
                    f.set(target, f.get(source));
            }
            else
            {
                f.set(target, f.get(source));
            }

        }
    }
}

