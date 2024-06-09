package Test.beans;

import Autowired.Cainsgl.annotations.Autowired;
import Autowired.Cainsgl.annotations.Component;
import Autowired.Cainsgl.annotations.Lazy;
import Autowired.Cainsgl.annotations.param.Formdata;
import Autowired.Cainsgl.annotations.param.PathVariable;
import Autowired.Cainsgl.annotations.proxy.EnableProxy;
import Autowired.Cainsgl.annotations.request.GetMapping;
import Autowired.Cainsgl.annotations.request.PostMapping;
import Autowired.Tools.Util.IO.FileUtil;
import Autowired.Tools.Util.IO.MultipartResolver;
import Test.beans.Mapper.Mappers;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.catalina.connector.CoyoteInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;


@EnableProxy
@Component
public class controller
{
    @Autowired
    Mappers mappers;

    @GetMapping("he")
    public testPojo testhe()
    {
        testPojo testPojo = new testPojo();
        testPojo.a=10;
        testPojo.b=20;
        return testPojo;
    }

    @PostMapping("test")
    public Integer test(HttpServletRequest request, @Formdata("abc") MultipartResolver.MutipartFile file , MultipartResolver resolver) throws IOException
    {
        FileUtil.LoadFile("测试的.txt",file.getData() );
        return 5;
    }
}
