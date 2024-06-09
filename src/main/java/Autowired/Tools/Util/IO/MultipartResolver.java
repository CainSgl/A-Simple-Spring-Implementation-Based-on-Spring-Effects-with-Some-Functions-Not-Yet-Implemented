package Autowired.Tools.Util.IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartResolver
{
    public static int Max_File_Size = 2 * 1024 * 1024;
    public static int DEFAULT_BUFFER_SIZE = 8192;
    jakarta.servlet.ServletInputStream inputStream;
    boolean IsResolved = false;

    public MultipartResolver(jakarta.servlet.ServletInputStream inputStream) throws IOException
    {
        this.inputStream = inputStream;
    }

    Map<String, MutipartFile> destryed;
    List<MutipartFile> files;

    public MultipartResolver resolveMultipart() throws IOException
    {
        if (IsResolved)
            return this;
        IsResolved = true;
        int len;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int n = 0;
        while ((len = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, len);
            n += len;
            if (n > Max_File_Size)
            {
                throw new IOException("文件超出大小，文件大小限制在:" + Max_File_Size / 1024 + "kb");
            }
        }
        matcherData(outputStream.toString("UTF-8"));
        return this;
    }

    public MutipartFile getPart(String name)
    {
        if(!IsResolved)
            throw new RuntimeException("使用MultipartResolver的getPart方法前，你没有调用resolveMultipart解析数据");
        MutipartFile m= destryed.get(name);
        if(m==null)
        {
            StringBuilder sb=new StringBuilder("form-data里没用对应的参数名的文件存在，有可能是输入错误\n传入的数据只有这些参数名:=> ( ");
            for(int i=0;i<files.size();i++)
            {
                sb.append(files.get(i).getName()).append(" , ");
            }
            sb.append(" ) ");
            throw new RuntimeException(sb.toString());
        }
        return m;
    }

    public List<MutipartFile> getAllPart()
    {
        return files;
    }

    private void matcherData(String data)
    {
        files = new ArrayList<>();
        destryed = new HashMap<>();
        Pattern partPattern = Pattern.compile(
                "----------------------------[^\\r\\n]+\\r\\n" + // 匹配边界
                        "Content-Disposition: form-data; name=\"(.*?)\"; filename=\"(.*?)\"\\r\\n" + // 文件名
                        "Content-Type: (.*?)\\r\\n\\r\\n" + // 内容类型
                        "(.*?)(?=----------------------------[^\\r\\n]+)", // 文件内容，非贪婪匹配直到下一个边界
                Pattern.DOTALL);
        Matcher matcher = partPattern.matcher(data);
        while (matcher.find())
        {
            String name = matcher.group(1);
            String fileName = matcher.group(2);
            String contentType = matcher.group(3);
            String content = matcher.group(4).trim();
            MutipartFile fileData = new MutipartFile(name,fileName, contentType, content);
            files.add(fileData);
            destryed.put(name, fileData);
        }
    }

    public static class MutipartFile
    {
        final String fileName;
        final String type;
        final String data;
        final String name;
        public MutipartFile(String name,String fileName, String type, String content)
        {
            this.fileName = fileName;
            this.type = type;
            this.data = content;
            this.name=name;
        }

        @Override
        public String toString()
        {
            return new StringBuilder("{\"name\":\"").append(name)
                    .append("\",\"fileName\":\"").append(fileName)
                    .append("\",\"type\":\"").append(type).append("\"}").toString();
        }

        public String getName()
        {
            return name;
        }
        public String getFileName()
        {
            return fileName;
        }

        public String getType()
        {
            return type;
        }

        public String getData()
        {
            return data;
        }
    }

}
