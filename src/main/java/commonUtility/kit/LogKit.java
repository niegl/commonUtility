package commonUtility.kit;

import commonUtility.log.IPlusLogger;
import commonUtility.log.PlusLoggerFactory;

/**
 * @author suisui
 * @version 1.2
 * @description 日志工具类
 * @date 2020/5/1 10:54
 * @since JavaFX2.0 JDK1.8
 */
public class LogKit {
    private static IPlusLogger logger;

    static {
        logger = PlusLoggerFactory.getLogger(LogKit.class);
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static void debug(Object message, Throwable t) {
        logger.debug(message, t);
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void info(Object message, Throwable t) {
        logger.debug(message, t);
    }

    public static void warn(Object message) {
        logger.warn(message);
    }

    public static void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    public static void error(Object message) {
        logger.error(message);
    }

    public static void error(Object message, Throwable t) {
        logger.error(message, t);
    }
}
