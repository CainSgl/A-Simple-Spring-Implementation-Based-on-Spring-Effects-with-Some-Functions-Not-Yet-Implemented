package Autowired.Cainsgl;

public class BeanException extends RuntimeException
{
    public BeanException(String message)
    {
        super("Bean错误："+message);
    }
}
