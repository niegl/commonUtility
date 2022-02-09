package commonUtility.utils;


import commonUtility.exception.ProtocolNotSupport;
import commonUtility.log.IPlusLogger;
import commonUtility.log.PlusLoggerFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author jack
 * @version 1.0
 * @date 2019/6/25 7:01
 * @since JavaFX2.0 JDK1.8
 */
public class FileUtil {
    private static final IPlusLogger logger = PlusLoggerFactory.getLogger(FileUtil.class);

    /**
     * @param filePath
     * @return 返回URL
     * @throws ProtocolNotSupport
     * @decription 从resources文件夹中读取File
     * 输出如：    file:/Users/suisui/workspace/Idea/JavaFX-Plus/target/classes/image/icon.png
     * @version 1.0
     */
    public InputStream getFilePathFromResources(String filePath) throws ProtocolNotSupport {
        return FileUtil.class.getResourceAsStream(filePath);
    }


    /**
     * @param filePath
     * @return
     * @description 读取resources文件夹下的file，相对于resources的文件路径，如 resources/config.conf 则只需 config.conf
     * @since 1.2.0 update: 使用getResourcesAsStream读取，屏蔽jar包读取障碍
     */
    public static String readFileFromResources(String filePath) throws UnsupportedEncodingException {
        InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        try (
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(inputStreamReader);
        ) {
            String temp;
            while ((temp = br.readLine()) != null) {
                // 一次读入一行数据
                content.append(temp + "\r\n");
            }
            return content.toString();
        } catch (IOException e) {
            logger.error("reading file error", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return "";
    }

    /**
     * @param filePath 绝对路径或相对路径
     * @return 返回文件内容
     * @description 读取文件
     */
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(filePath);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String temp;
            while ((temp = br.readLine()) != null) {
                // 一次读入一行数据
                content.append(temp).append("\r\n");
            }
        } catch (IOException e) {
            logger.error("reading file error", e);
        }
        return content.toString();
    }

    /**
     * @param filePath 写出文件的地址
     * @param content  文件内容
     * @description 写文件
     */
    public static void writeFile(String filePath, String content) {
        try {
            File writeName = new File(filePath); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(content);
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
