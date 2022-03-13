package commonUtility.log;

import commonUtility.kit.PathKit;
import org.apache.log4j.*;

/**
 * log工厂类
 */
public class PlusLoggerFactory {

    private PlusLoggerFactory() {
    }

    public static IPlusLogger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getSimpleName());
        logger.removeAllAppenders();
        // 設定Logger級別。
        logger.setLevel(Level.DEBUG);
        // 設定是否繼承父Logger。
        // 默認為true。繼承root輸出。
        // 設定false後將不輸出root。
        logger.setAdditivity(true);
        // 生成新的Appender
        FileAppender appender = new RollingFileAppender();
        PatternLayout layout = new PatternLayout();
        // log的输出形式
        String conversionPattern = "[%d] %p %t %c - %m%n";
        layout.setConversionPattern(conversionPattern);
        appender.setLayout(layout);
        // log输出路径
        // 这里使用了环境变量[catalina.home]，只有在tomcat环境下才可以取到
        String homeDir = PathKit.getSystemHomeDir();
        appender.setFile(homeDir + "/logs/" + clazz.getSimpleName() + ".log");
        // log的文字码
        appender.setEncoding("UTF-8");
        // true:在已存在log文件后面追加 false:新log覆盖以前的log
        appender.setAppend(true);
        // 适用当前配置
        appender.activateOptions();
        // 将新的Appender加到Logger中
        logger.addAppender(appender);
        return new PlusLogger(logger);
    }

}
