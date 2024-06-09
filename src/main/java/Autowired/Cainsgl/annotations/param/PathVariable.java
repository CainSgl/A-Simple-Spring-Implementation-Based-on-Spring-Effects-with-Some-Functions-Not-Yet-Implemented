package Autowired.Cainsgl.annotations.param;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PathVariable
{
    String value();
}
