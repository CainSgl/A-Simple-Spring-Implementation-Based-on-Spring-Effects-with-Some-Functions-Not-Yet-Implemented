package Autowired.Tools.Util.IO;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeProcessor
{
    final static Map<String, String> mimeTypeMap = new HashMap<>();
    final static Map<String,String> extensionMap = new HashMap<>();
    static
    {
        mimeTypeMap.put("jpg", "image/jpeg");
        mimeTypeMap.put("jpeg", "image/jpeg");
        mimeTypeMap.put("png", "image/png");
        mimeTypeMap.put("gif", "image/gif");
        mimeTypeMap.put("css", "text/css");
        mimeTypeMap.put("js", "application/javascript");
        mimeTypeMap.put("html", "text/html");
        mimeTypeMap.put("json", "application/json");
        mimeTypeMap.put("pdf", "application/pdf");
        mimeTypeMap.put("txt", "text/plain");
        mimeTypeMap.put("xml", "application/xml");
        mimeTypeMap.put("svg", "image/svg+xml");
        mimeTypeMap.put("mp3", "audio/mpeg");
        mimeTypeMap.put("mp4", "video/mp4");
        mimeTypeMap.put("webm", "video/webm");
        mimeTypeMap.put("ico", "image/x-icon");
        mimeTypeMap.put("bmp", "image/bmp");
        mimeTypeMap.put("doc", "application/vnd.ms-word");
        mimeTypeMap.put("docx", "application/vnd.ms-word");
        mimeTypeMap.put("xls", "application/vnd.ms-excel");
        mimeTypeMap.put("xlsx", "application/vnd.ms-excel");
        mimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypeMap.put("pptx", "application/vnd.ms-powerpoint");
        for (Map.Entry<String, String> entry : mimeTypeMap.entrySet()) {
            extensionMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static String getMimeType(String key)
    {
        key = key.toLowerCase();
        String s = mimeTypeMap.get(key);
        if (s == null)
            return "application/octet-stream";
        else
            return s;
    }
    public static String  getFileSuffix(String contentType)
    {
        contentType=contentType.toLowerCase();
        String s = extensionMap.get(contentType);
        if (s == null)
            return "null";
        else
            return s;
    }
}
