package Autowired.Tools.TomcatServer;


import Autowired.BeanGetter.ProxyFactor.ProxyFactor;

import Autowired.Cainsgl.annotations.param.Formdata;
import Autowired.Cainsgl.annotations.param.PathVariable;
import Autowired.Cainsgl.annotations.param.RequsetBody;

import Autowired.Tools.Util.IO.ContentTypeProcessor;
import Autowired.Tools.Util.IO.MultipartResolver;
import com.alibaba.fastjson.JSONObject;


import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Session;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;


import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


import static Autowired.Tools.BeansManager.*;

public class SummerHttpServelet
{
    public SummerHttpServelet(String port, String filepath)
    {
        this.port = port;
        this.len = port.length() + 1;
        this.filepath = filepath;
        //1是请求，2是响应，3是MultipartResolver
        getElement.put(jakarta.servlet.ServletRequest.class, 1);
        getElement.put(jakarta.servlet.http.HttpServletRequest.class, 1);
        getElement.put(org.apache.catalina.connector.RequestFacade.class, 1);

        getElement.put(jakarta.servlet.ServletResponse.class, 2);
        getElement.put(jakarta.servlet.http.HttpServletResponse.class, 2);
        getElement.put(org.apache.catalina.connector.ResponseFacade.class, 2);

        getElement.put(Autowired.Tools.Util.IO.MultipartResolver.class, 3);
    }

    public static String filepath;
    final String port;
    final int len;
    final Map<Class<?>, Integer> getElement = new HashMap<>();

    public void SetJSON(ResponseFacade response)
    {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setContentType("application/json");
    }

    public void PrintSucess(ResponseFacade response, Object data)
    {
        try
        {
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.toJSONString(data));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void PrintError(ResponseFacade response, String path, String message)
    {
        String escapedMessage;
        if (message != null)
            escapedMessage = message.replace("\\", "\\\\").replace("\"", "\\\"");
        else
            escapedMessage = "null";
        try
        {
            PrintWriter writer = response.getWriter();
            writer.println("\"" + escapedMessage + "\"");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (Exception e2)
        {
            System.err.println("在服务器发送错误的时候无法拿到请求的Writer");
            e2.printStackTrace();
        }
    }

    public void PrintError(ResponseFacade response, String path)
    {
        PrintError(response, path, "No message available");
    }

    public void Print404(ResponseFacade response, String requestpath)
    {
        try
        {
            PrintWriter writer = response.getWriter();
            writer.println("{\"status\":\"404\",\"error\": \"找不到页面\",\"message\": \"No message available\",\"path\":\"" + requestpath + "\"}");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (Exception e2)
        {
            System.err.println("在服务器发送错误的时候无法拿到请求的Writer");
            e2.printStackTrace();
        }
    }

    public Object parseStringToType(Class<?> Type, String value, Object caller, Method method)
    {
        if (Type == String.class)
        {
            return value;
        }
        if (Type == int.class || Type == Integer.class)
        {
            return Integer.parseInt(value);
        }
        if (Type == Double.class || Type == double.class)
        {
            return Double.parseDouble(value);
        }
        if (Type == Long.class || Type == long.class)
        {
            return Long.parseLong(value);
        }
        if (Type == Float.class || Type == float.class)
        {
            return Float.parseFloat(value);
        }
        System.err.println("这里的Controller的方法出现错误=>(" + caller.getClass().getName() + ".java.0)" + "\n方法：" + method);
        System.err.println("原因：@PathVariable只支持int,double,float,long四种类型");
        return null;
    }

    public int getParmas(Object[] params, Parameter[] parameters, RequestFacade request, Object caller, Method method, ResponseFacade response)
    {
        int lend = 0;
        MultipartResolver resolver = null;
        if (request.getContentType() != null)
        {
            if (request.getContentType().startsWith("multipart/form-data"))
            {
                try
                {
                    resolver = new MultipartResolver(request.getInputStream());
                }
                catch (Exception e)
                {
                    System.err.println("获取请求的输入流的时候失败\n在调用=>" + method.getName() + "  的时候");
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < parameters.length; i++)
        {
            Class<?> type = parameters[i].getType();
            RequsetBody requestBody = parameters[i].getAnnotation(RequsetBody.class);
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            Formdata formdata = parameters[i].getAnnotation(Formdata.class);
            if (pathVariable != null)
            {
                params[i] = parseStringToType(type, request.getParameter(pathVariable.value()), caller, method);
                lend++;
                continue;
            }
            if (requestBody != null)
            {
                try
                {
                    JSONObject tryParse = (JSONObject) JSONObject.parse(request.getParameter(requestBody.value()));
                    if (tryParse == null)
                    {
                        params[i] = null;
                        System.err.println("无法解析数据，解析出来为空对象:\n数据格式=>" + request.getParameter(requestBody.value()));
                    }
                    else
                    {
                        try
                        {
                            Field[] fields = type.getDeclaredFields();
                            params[i] = type.getConstructor().newInstance();
                            //自动装配
                            for (Field field : fields)
                            {
                                field.setAccessible(true);
                                field.set(params[i], tryParse.get(field.getName()));
                            }
                        }
                        catch (Exception e)
                        {
                            System.err.println("你提供的数据格式应该提供一个无参构造器" + e.getMessage());
                        }
                    }
                }
                catch (Exception e)
                {
                    System.out.println("在把发送的数据解析为Object的时候出现了错误\n数据格式=>" + request.getParameter(requestBody.value()));
                }
                lend++;
                continue;
            }
            if (formdata != null)
            {
                if (type != MultipartResolver.MutipartFile.class)
                {
                    System.err.println("错误的,@Formdata只能使用在MultipartResolver.MutipartFile上\n该方法的参数列表不正确:=>" + method);
                    params[i] = null;
                }
                else
                {
                    try
                    {

                        if (resolver != null)
                        {
                            params[i] = resolver.resolveMultipart().getPart(formdata.value());
                            lend++;
                        }
                        else
                        {
                            throw new Exception("非法的数据请求，格式不为multipart/form-data,无法获取里面的数据\n在调用方法:=>" + method);
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println("在获取formdata里的数据的时候出现异常\n请检查传来的数据是否正常\n在调用方法:=>" + method);
                        e.printStackTrace();
                    }
                }
                continue;
            }
            Integer integer = getElement.get(type);
            if (integer == null)
            {
                System.err.println("在方法的参数上你必须使用@RequsetBody注解这是个Object或者@PathVariable标记这是基本类型");
                params[i] = null;
                continue;
            }
            switch (integer)
            {
            case 1:
                params[i] = request;
                lend++;
                break;
            case 2:
                params[i] = response;
                lend++;
                break;
            case 3:
                params[i] = resolver;
                lend++;
                break;
            default:
                System.err.println("在方法的参数上你必须使用@RequsetBody注解这是个Object或者@PathVariable标记这是基本类型");
                params[i] = null;
            }
        }
        return lend;
    }

    public void CallFunction(Method method, RequestFacade request, ResponseFacade response, String path)
    {
        if (method == null)
        {
            Print404(response, path);
        }
        else
        {
            SetJSON(response);
            method.setAccessible(true);
            Object caller = ProxyFactor.MethodProxy.get(method);
            if (caller != null)
            {
                Parameter[] parameters;
                parameters = method.getParameters();
                if (ProxyFactor.ObjectProxy.containsKey(caller))
                {
                    caller = ProxyFactor.ObjectProxy.get(caller);
                    System.out.println("客户端请求，该对象为Proxy，已从Proxy池里拿到代理对象\n");
                }
                else
                {
                    System.out.println();
                }
                Object[] params = new Object[parameters.length];
                Object data = null;
                try
                {
                    int lend = getParmas(params, parameters, request, caller, method, response);
                    data = method.invoke(caller, params);//其实应该返回特殊的形式，携带函数是否执行成功的
                    if (lend == parameters.length)
                    {
                        System.out.println(
                                "\n方法回调成功：需要的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(parameters) +
                                        "\n          :装配的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(params) + "\nreturn:" + JSONObject.toJSONString(data)
                        );
                    }
                    else
                    {
                        System.err.println(
                                "\n方法回调错误：需要的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(parameters) +
                                        "\n          :装配的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(params) + "\nreturn:" + JSONObject.toJSONString(data)
                        );
                    }
                    PrintSucess(response, data);
                }
                catch (Exception e)
                {
                    System.err.println("方法调用的时候出现错误！");
                    System.err.println(
                            "\n方法调用出错：需要的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(parameters) +
                                    "\n          :装配的参数列表:=>\t" + method.getName() + ProxyFactor.toParamsString(params) + "\nreturn:" + JSONObject.toJSONString(data)
                    );
                    if (e.getCause() != null)
                        PrintError(response, path, e.getMessage());
                    else if (data != null)
                    {
                        PrintError(response, path, data.toString());
                    }
                    else
                    {
                        PrintError(response, path, "未知的错误");
                    }
                    e.printStackTrace();
                }
            }
            else
            {
                Print404(response, path);
            }
        }
    }

    //差不多就是这么多，源码就不分析了
    public String GetPath(StringBuffer path)
    {
        int a = path.indexOf(port) + len;
        return path.substring(a);
    }

    public void doOther(RequestFacade req, ResponseFacade res, StringBuffer path)
    {
        String requestPath = GetPath(path);
        System.out.println("请求了Other的" + path);
    }


    public void doGet(RequestFacade request, ResponseFacade response, StringBuffer path)
    {
        String requestPath = GetPath(path);
        if (requestPath.contains("."))
        {
            getFile(request, response, requestPath);
            return;
        }
        Method method = Get_URL_CALL.get(requestPath);
        //TODO
        setCors(response);
        CallFunction(method, request, response, requestPath);
    }


    public void getFile(RequestFacade request, ResponseFacade response, String path)
    {
        if (filepath == null)
        {
            return;
        }
        String format = path.substring(path.lastIndexOf(".") + 1, path.length());
        String getFilePath = filepath + "\\" + path.replace("/", "\\");
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(getFilePath)))
        {
            byte[] buffer = new byte[1024];
            OutputStream servletOutputStream = response.getOutputStream();
            int numChars;
            while ((numChars = inputStream.read(buffer)) != -1)
            {
                servletOutputStream.write(buffer, 0, numChars);
            }
            response.setContentType(ContentTypeProcessor.getMimeType(format));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintError(response, path);
            System.out.println(filepath + "\\" + path.replace("/", "\\") + "被请求了，但是无法获取对应的输出流对象");
        }
    }

    void setCors(ResponseFacade response)
    {
        response.setHeader("Access-Control-Allow-Origin", "*");
    }

    public void doPost(RequestFacade request, ResponseFacade response, StringBuffer path)
    {
        String requestPath = GetPath(path);
        Method method = Post_URL_CALL.get(requestPath);
        setCors(response);
        CallFunction(method, request, response, requestPath);
    }

}
