package commonUtility.utils;


import org.apache.commons.lang3.StringUtils;
import commonUtility.log.IPlusLogger;
import commonUtility.log.PlusLoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * @author jack
 * @author suisui
 * @version 1.0
 * @date 2019/6/25 3:46
 * @since JavaFX2.0 JDK1.8
 * @since 1.3.0 add：继承StringUtils
 */
public class StringUtil extends StringUtils {
    private static IPlusLogger logger = PlusLoggerFactory.getLogger(StringUtil.class);

    private StringUtil() {
    }

    /**
     * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) throws UnsupportedEncodingException {
        String fileUrl = URLDecoder.decode(url.getFile(), CharsetConstant.DEFAULT_CHARSET);
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * "cn.fh.lightning" -> "cn/fh/lightning"
     *
     * @param name
     * @return
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * "cn/fh/lightning" -> "cn.fh.lightning"
     *
     * @param name
     * @return
     */
    public static String splashToDot(String name) {
        return name.replaceAll("/", "\\.");
    }

    /**
     * "Apple.class" -> "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.lastIndexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }
        return name;
    }

    /**
     * /application/home -> /home
     *
     * @param uri
     * @return
     */
    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');
        return trimmed.substring(splashIndex);
    }

    /**
     * MainController$receive -> MainController
     *
     * @param name
     * @return
     */
    public static String getBaseClassName(String name) {
        int index = name.indexOf("$");
        if (index == -1) {
            return name;
        }
//        System.out.println(name.substring(0, index));
        return name.substring(0, index);
    }


    /**
     * Object -> object ; Student -> student
     *
     * @param name
     * @return
     */
    public static String toInstanceName(String name) {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }

    /**
     * object -> Object ; student -> Student
     *
     * @param name
     * @return
     */
    public static String toClassName(String name) {
        return name.substring(0, 1).toUpperCase().concat(name.substring(1));
    }

    /**
     * cn/edu/scau/biubiusuisui/resources/fxml/languageDemo/langDemo.fxml -> fxml/languageDemo/langDemo.fxml
     *
     * @param name
     * @return
     * @description 获取相对于resources目录下的路径
     */
    public static String getFilePathInResources(String name) {
        String resources = "resources";
        int resIdx = name.indexOf(resources);
        if (resIdx == -1) {
            return name;
        }
        return name.substring(resIdx + resources.length() + 1);
    }

    /**
     * cn/edu/scau/biubiusuisui/resources/fxml/languageDemo/langDemo.fxml -> languageDemo
     *
     * @param name 文件名
     * @return
     * @version 1.2
     */
    public static String getFileBaseName(String name) {
        String result = "";
        String[] tempStrs = name.split("/");
        if (1 == tempStrs.length) { //只有文件名，即name: langDemo.fxml
            result = StringUtil.trimExtension(name);
        } else {
            result = StringUtil.trimExtension(tempStrs[tempStrs.length - 1]);
        }
        return result;
    }

}