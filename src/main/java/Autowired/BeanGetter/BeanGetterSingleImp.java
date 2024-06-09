package Autowired.BeanGetter;

import Autowired.BeanGetter.ProxyFactor.ProxyFactor;
import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Tools.AppInitializer;
import Autowired.Tools.BeansManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

//实例化，这里是实例化的Bean,也就是singleObject是Component注解的类，这里是直接把他实例化

public class BeanGetterSingleImp implements BeanGetter
{
    public BeanGetterSingleImp(Constructor<?> constructor) throws ReflectiveOperationException
    {
        constructor.setAccessible(true);
        singleObject = constructor.newInstance();
        AppInitializer.beansManager.InjectBean(singleObject, singleObject);
        //自注入自己，产生代理对象，这样不使用AutoWired也能产生代理对象
        AppInitializer.proxyFactor.putProxy(this.getClass().getDeclaredFields()[0], this);
        Method[] Methods = singleObject.getClass().getMethods();
        for(Method m : Methods)
        {
            ProxyFactor.MethodProxy.put(m,singleObject);
        }
    }

    public BeanGetterSingleImp(Constructor<?> constructor, Object... wirtingObject) throws ReflectiveOperationException
    {
        constructor.setAccessible(true);
        singleObject = constructor.newInstance();
        Object[] newWritingObjects = ExpandArry(wirtingObject);
        AppInitializer.beansManager.InjectBean(singleObject, newWritingObjects);
    }

    private Object[] ExpandArry(Object[] wirtingObject)
    {
        Object[] newWritingObjects = new Object[wirtingObject.length + 1];
        System.arraycopy(wirtingObject, 0, newWritingObjects, 0, wirtingObject.length);
        newWritingObjects[newWritingObjects.length - 1] = singleObject;
        return newWritingObjects;
    }

    Object singleObject;

    @Override
    public Object getBean()
    {
        return singleObject;
    }


}
