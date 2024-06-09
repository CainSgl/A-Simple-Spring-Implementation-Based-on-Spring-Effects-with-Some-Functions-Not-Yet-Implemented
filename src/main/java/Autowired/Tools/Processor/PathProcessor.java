package Autowired.Tools.Processor;

import Autowired.Cainsgl.BeanRuntimeException;

import java.io.File;

public class PathProcessor
{
    private static String BootStrapDirectory;

    public static void SetBootStrapDirectory(String BootStrapDirectory)
    {
        PathProcessor.BootStrapDirectory = BootStrapDirectory;
    }

    public static String getBootStrapDirectory()
    {
        return BootStrapDirectory;
    }

    public static String getPackageName(String fullClassName) throws BeanRuntimeException
    {
        if (fullClassName.indexOf('.') == -1)
        {
            throw new BeanRuntimeException("无法找到对应的软件包，你传入的Class对象应该是在一个子包下", 2);
        }
        return fullClassName.substring(0, fullClassName.lastIndexOf('.'));
    }

    private static int DirectoryParentAt;

    public static void InitBoot(File file)
    {
        String beanAbName = file.getAbsolutePath();
        DirectoryParentAt = beanAbName.indexOf(BootStrapDirectory);
    }

    public static String getPackageNameByABPath(String beanAbName)
    {
        if (!beanAbName.contains(".class"))
            return null;
        return beanAbName.substring(DirectoryParentAt, beanAbName.lastIndexOf(".class")).replace("\\", ".");
    }
}
