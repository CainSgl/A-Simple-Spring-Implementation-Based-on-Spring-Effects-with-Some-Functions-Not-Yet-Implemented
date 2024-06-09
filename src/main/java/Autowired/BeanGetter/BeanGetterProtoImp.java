package Autowired.BeanGetter;

import Autowired.Cainsgl.BeanRuntimeException;
import Autowired.Tools.AppInitializer;


import java.lang.reflect.Constructor;


public class BeanGetterProtoImp implements BeanGetter
{
    Constructor<?> constructor;
    public BeanGetterProtoImp(Constructor<?> constructor) throws ReflectiveOperationException
    {
        this.constructor = constructor;
    }
    public BeanGetterProtoImp(Constructor<?> constructor,Object... wirtingObject) throws ReflectiveOperationException
    {
        writingObjects=wirtingObject;
        this.constructor = constructor;
    }
    Object[] writingObjects;

    @Override
    public Object getBean()
    {
        Object bean = null;
        try{
            constructor.setAccessible(true);
            bean=constructor.newInstance();
        }catch (Exception e) {
           e.printStackTrace();
        }
        if(bean==null)
        {
            throw  new BeanRuntimeException("构造器生成实例失败",1);
        }
        AppInitializer.beansManager.InjectBean(bean,false,bean);
        return bean;
    }

    public void BeanToProxy(Object proxy)
    {
        //TODO 后面再支持
        throw new BeanRuntimeException("目前不支持原型模式的代理",1);
    }
}
