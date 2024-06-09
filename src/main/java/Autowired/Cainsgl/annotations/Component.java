package Autowired.Cainsgl.annotations;

import Autowired.Cainsgl.ScopeMode;

import java.lang.annotation.*;

/**
 * <h3>作为Bean</h3>
 * <p>该注解可以接收mode的值，但只能是Singleton或Prototype</p>
 * <a href="https://qm.qq.com/q/yIa89unkAM">有疑问点我加作者QQ</a>
 *
 * @author Cainsgl
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Component
{
    ScopeMode Scope() default ScopeMode.Singleton;
}

