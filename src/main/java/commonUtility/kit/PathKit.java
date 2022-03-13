package commonUtility.kit;

import commonUtility.exception.ProtocolNotSupport;

import java.io.File;
import java.io.InputStream;

/**
 * @author suisui
 * @version 1.2
 * @description 路径工具类
 * @date 2020/5/2 14:43
 * @since JDK1.8
 */
public class PathKit {
    public static String getSystemHomeDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 从resources文件夹中读取File
     * @param filePath
     * @throws ProtocolNotSupport
     * @return file:/Users/suisui/workspace/Idea/JavaFX-Plus/target/classes/image/icon.png
     * @version 1.0
     */
    public static InputStream getFilePathFromResources(String filePath) throws ProtocolNotSupport {
        return FileKit.class.getResourceAsStream(filePath);
    }
    /**
     * 判断文件/路径是否存在
     */
    public static boolean fileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
