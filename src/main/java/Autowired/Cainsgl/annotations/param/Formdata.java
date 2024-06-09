package Autowired.Cainsgl.annotations.param;

import java.lang.annotation.*;


/**
 * <p>只能注解在MultipartResolver.MutipartFile上</p>
 * @author Cainsgl
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Formdata
{
    String value();
}
