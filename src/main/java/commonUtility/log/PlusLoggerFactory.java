package commonUtility.log;

import org.apache.log4j.Logger;

/**
 * log工厂类
 */
public class PlusLoggerFactory {

    static {
        initLog4jBase();
    }

    private PlusLoggerFactory() {
    }

    public static IPlusLogger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz);
        return new PlusLogger(logger);
    }

    /**
     * 初始化log4j的路径,如果没有设置那么默认在当前路径下.
     */
    public static void initLog4jBase() {
        if (System.getProperty("log.base") == null) {
            String projectPath = System.getProperty("user.dir");;
            System.setProperty("log.base", projectPath);
        }
    }

}
