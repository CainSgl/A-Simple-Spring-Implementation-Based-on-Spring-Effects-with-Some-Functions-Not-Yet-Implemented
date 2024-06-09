package Autowired.BeanGetter.FunctionFactor;

import Autowired.BeanGetter.BeanGetter;
import Autowired.Tools.BeansManager;

import java.lang.reflect.Method;

public class BeanFactorSingleImp implements BeanGetter
{
    public BeanFactorSingleImp(Method method, Object invoker) throws ReflectiveOperationException
    {
        method.setAccessible(true);
        singleObject=method.invoke(invoker);
        Class<?> clazz=singleObject.getClass();
        BeansManager.BEGETTER_POOL.put(clazz.getTypeName(),this);
    }
    Object singleObject;
    @Override
    public Object getBean()
    {
        return singleObject;
    }


}
