package Autowired.Cainsgl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 <h3>配置信息</h3>
 <p>需要实现一个接口getConfigures</p>
 <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
 * @author Cainsgl
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config
{
}
