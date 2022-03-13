package commonUtility.log;

import commonUtility.kit.LogKit;
import org.junit.jupiter.api.Test;

class IPlusLoggerTest {

    @Test
    void info() {
        LogKit.info("test");
    }
}