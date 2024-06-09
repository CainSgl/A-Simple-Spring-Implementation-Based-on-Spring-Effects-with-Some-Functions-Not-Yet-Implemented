package Autowired.Tools.InfoLog;

import java.lang.reflect.Method;
import java.util.Arrays;

public interface PrintHttpInfo
{
    default void PrintGet(Method m,Class<?>[] params,Object... paramsObj)
    {
        System.out.println(m.getName()+"参数列表\n\t"+ Arrays.toString(params)
                +"\n对应的参数\n\t"+ Arrays.toString(paramsObj));
    }
    default void PrintPost(Method m,Class<?>[] params,Object... paramsObj)
    {
        System.out.println(m.getName()+"参数列表\n\t"+ Arrays.toString(params)
        +"\n对应的参数\n\t"+ Arrays.toString(paramsObj));
    }
}
