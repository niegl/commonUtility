package commonUtility.log;

import commonUtility.utils.LogUtil;
import org.junit.jupiter.api.Test;

class IPlusLoggerTest {

    @Test
    void info() {
        LogUtil.info("test");
    }
}