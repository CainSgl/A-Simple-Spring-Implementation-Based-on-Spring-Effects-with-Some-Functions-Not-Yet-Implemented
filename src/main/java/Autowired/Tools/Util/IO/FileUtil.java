package Autowired.Tools.Util.IO;

import Autowired.Tools.TomcatServer.SummerHttpServelet;

import java.io.*;

public class FileUtil
{
    public static void LoadFile(String fileName, MultipartResolver.MutipartFile mutipartFile) throws IOException
    {
        LoadFile(fileName, mutipartFile, SummerHttpServelet.filepath+"\\");
    }

    public static void LoadFile(String fileName, String data) throws IOException
    {
        LoadFile(fileName, data, SummerHttpServelet.filepath+"\\");
    }

    public static void LoadFile(String fileName, MultipartResolver.MutipartFile mutipartFile, String path) throws IOException
    {
        LoadFile(fileName, mutipartFile.getData(), path);
    }

    public static void LoadFile(String fileName, String data, String path) throws IOException
    {
        byte[] bytes = data.getBytes("UTF-8");
        File file = new File(path+fileName);
        if(!file.exists())
        {
            file.createNewFile();
        }
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        outputStream.write(bytes);
        outputStream.close();
    }
}
