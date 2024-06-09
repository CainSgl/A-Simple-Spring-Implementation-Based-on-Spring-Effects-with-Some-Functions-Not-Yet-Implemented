package Autowired.Tools.TomcatServer;


import Autowired.Cainsgl.BeanRuntimeException;


import Autowired.Tools.AppInitializer;
import jakarta.servlet.*;
import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;

import org.apache.catalina.servlets.CGIServlet;
import org.apache.catalina.startup.Tomcat;
import java.io.UnsupportedEncodingException;


public class ServerInitializer
{
    //默认的
    public int port = 8080;

    String filePath;
    public void InitializeServer()
    {

        Tomcat tomcat = new Tomcat();
        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");
        Connector connector = new Connector();
        connector.setPort(port);
        Host host = new StandardHost();
        host.setName("localhost");
        if(AppInitializer.getConfigures!=null)
        {
             filePath=AppInitializer.getConfigures.getFilePath();
        }

        Engine engine = new StandardEngine();
        engine.setDefaultHost("localhost");
        engine.setName("localhost");
        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());

        host.addChild(context);
        engine.addChild(host);
        service.setContainer(engine);
        service.addConnector(connector);

        tomcat.addServlet("", "dispatcher", new Servlet()
        {
            SummerHttpServelet summerHttpServelet;

            @Override
            public void init(ServletConfig servletConfig) throws ServletException
            {
                summerHttpServelet=new SummerHttpServelet( String.valueOf(port),filePath);
            }

            @Override
            public ServletConfig getServletConfig()
            {
                return new CGIServlet();
            }

            @Override
            public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws UnsupportedEncodingException
            {
                //这里实际上是tomcat通过适配器传过来的一个httpServletRequest
                RequestFacade requestFacade=(RequestFacade)servletRequest;
                ResponseFacade responseFacade=(ResponseFacade)servletResponse;
                requestFacade.setCharacterEncoding("utf-8");
                responseFacade.setCharacterEncoding("utf-8");
                String method = requestFacade.getMethod();
                StringBuffer stringBuilder=requestFacade.getRequestURL();
                System.out.println("\n请求方式"+method+"\n请求的URL:=>\t"+stringBuilder);
               if(method.equals("GET"))
               {
                   summerHttpServelet.doGet(requestFacade,responseFacade,requestFacade.getRequestURL());
               }else  if(method.equals("POST"))
               {
                   summerHttpServelet.doPost(requestFacade,responseFacade,requestFacade.getRequestURL());
               }else
               {
                   summerHttpServelet.doOther(requestFacade,responseFacade,requestFacade.getRequestURL());
               }
            }
            @Override
            public String getServletInfo()
            {
                return "";
            }

            @Override
            public void destroy()
            {
                summerHttpServelet = null;
            }
        });
        context.addServletMappingDecoded("/*", "dispatcher");
        try
        {
            tomcat.start();
            System.out.println("--------Tomcat startup success-------");
            System.out.println("Port:" + port + "\t\tVersion:10.1.24");
            System.out.println("-------------------------------------");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BeanRuntimeException("服务器启动失败！", 1);
        }
    }
}
