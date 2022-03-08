package commonUtility.utils;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilTest {

    @org.junit.jupiter.api.Test
    void getSystemHomeDir() {
        System.out.println(PathUtil.getSystemHomeDir());
    }
}