package Autowired.Cainsgl.annotations.proxy;

import java.lang.annotation.*;

/**
 * <p>不太推荐用，会额外的消耗资源</p>
 * <p>需要搭配Componet注解使用</p>
 * @author Cainsgl
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableProxy
{
}
