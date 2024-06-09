package Autowired.Cainsgl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 <h3>惰性注入</h3>
 <p>他只能注解字段，注解后，如果是一个他的类上面有Component注解，会自动注入对应的实例</p>
 <p>使用他后，你必须同时也使用AutoWired，否则没有效果,他是在第二次注入的时候在引入值，使用他你可以避免循环注入</p>
 <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
 * @author Cainsgl
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lazy
{
}
