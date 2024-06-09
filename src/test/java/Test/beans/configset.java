package Test.beans;

import Autowired.Cainsgl.annotations.Config;
import Autowired.Tools.checker.getConfigures;

@Config
public class configset implements getConfigures
{
    @Override
    public int getPort()
    {
        return 8081;
    }

    @Override
    public String getFilePath()
    {
        return "C:\\Users\\pxj\\Desktop\\File";
    }

    @Override
    public String getSQLaccount()
    {
        return "";
    }

    @Override
    public String getSQLpassword()
    {
        return "";
    }

    @Override
    public String getDateBase()
    {
        return "";
    }

    @Override
    public String getMybatisXML()
    {
        return "C:\\Users\\pxj\\Desktop\\AutoWired\\src\\main\\resources\\mybatisconfig.xml";
    }
}
