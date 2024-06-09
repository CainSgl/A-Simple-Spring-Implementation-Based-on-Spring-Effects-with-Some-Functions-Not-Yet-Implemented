package Autowired.BeanGetter.FunctionFactor;

import Autowired.BeanGetter.BeanGetter;


import java.lang.reflect.Method;

public class BeanFactorProtoImp implements BeanGetter
{
    Method method;
    Object Factor;
    public BeanFactorProtoImp(Method method,Object Factor) throws ReflectiveOperationException
    {
        this.method = method;
        this.Factor=Factor;
        Object o=method.invoke(Factor);
    }


    @Override
    public Object getBean()
    {
       Object o=null;
       try{
           o=method.invoke(Factor);
       }catch (Exception e)
       {
           e.printStackTrace();
       }
       return o;
    }


}
