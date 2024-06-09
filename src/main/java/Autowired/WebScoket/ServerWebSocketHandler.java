package Autowired.WebScoket;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ServerWebSocketHandler
{
    String path();
}
