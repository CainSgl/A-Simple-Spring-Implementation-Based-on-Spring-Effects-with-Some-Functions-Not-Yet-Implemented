package Autowired.Cainsgl;

import java.io.PrintStream;
import java.io.PrintWriter;

public class BeanRuntimeException extends RuntimeException
{
    //这里是两种错误
    public int ErrorInfo;
    public BeanRuntimeException(String message,int ErrorInfo)
    {
        super(message);
        this.ErrorInfo = ErrorInfo;
    }

    @Override
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintWriter s)
    {
        s.println(getMessage());
        StackTraceElement[] stackTrace = this.getStackTrace();
        int len=stackTrace.length-ErrorInfo;
        for(int i=len;i>0;i--)
        {
            s.println(stackTrace[i].toString());
        }
    }

    @Override
    public void printStackTrace(PrintStream s)
    {
        s.println(getMessage());
        StackTraceElement[] stackTrace = this.getStackTrace();
        int len=stackTrace.length-ErrorInfo;
        for(int i=len;i>0;i--)
        {
            s.println(stackTrace[i].toString());
        }
    }
}
