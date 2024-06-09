package Autowired.Cainsgl.annotations;

import java.lang.annotation.*;

/**
 <h3>自动注入</h3>
 <p>他只能注解字段，注解后，如果是一个他的类上面有Component注解，会自动注入对应的实例</p>
 <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
 * @author Cainsgl
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Autowired
{

}
