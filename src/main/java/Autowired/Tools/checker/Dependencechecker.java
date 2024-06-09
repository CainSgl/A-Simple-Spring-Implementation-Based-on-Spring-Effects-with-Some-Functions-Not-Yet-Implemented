package Autowired.Tools.checker;


import Autowired.BeanGetter.ProxyFactor.ProxyFactor;
import Autowired.Tools.AppInitializer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Dependencechecker
{
    public SqlSession sqlSession;
    public HashMap<Field,Object> MapperWillAutoWired=new HashMap<>();

    public void CheckDependence(/*ClassLoader classLoader*/)
    {
        //TODO 这里动态配置涉及的东西太多了，为了偷懒，直接使用mybatis吧
        if(AppInitializer.getConfigures==null)
            return;
        getConfigures instance= AppInitializer.getConfigures;
        String resource=instance.getMybatisXML();
        if(resource==null||resource.equals(""))
            return;
        try(InputStream inputStream =new BufferedInputStream(new FileInputStream(resource)))
        {

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            sqlSession = sqlSessionFactory.openSession();
        }catch (Exception e){
            System.err.println("无法获取到InputStream,请检查你返回的资源文件路径是否正确，注意携带后缀！");
           e.printStackTrace();
        }
    }
    public void WiredAllMapper()
    {
        if(MapperWillAutoWired==null)
            return;
        if(sqlSession==null)
            return;
        for(Map.Entry<Field,Object> entry:MapperWillAutoWired.entrySet())
        {
            Field key = entry.getKey();
            Object value=entry.getValue();
            //看是不是代理对象
           if(ProxyFactor.ObjectProxy.containsKey(value))
           {
               value=ProxyFactor.ObjectProxy.get(value);
           }
            key.setAccessible(true);
            try{
                System.out.println(key);
                key.set(value,MapperFactor(key.getType()));
            }catch (Exception e){
                System.err.println("\nMapper注入失败"+value.getClass().getTypeName()+"."+key.getName()+"\n他的类型是"+key.getType().getSimpleName() );
                e.printStackTrace();
            }
        }
    }
    public <T>T MapperFactor(Class<T> tClass)
    {
        return sqlSession.getMapper(tClass);
    }

}
