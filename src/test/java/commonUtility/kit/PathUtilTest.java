package commonUtility.kit;

import commonUtility.file.PathKit;

class PathUtilTest {

    @org.junit.jupiter.api.Test
    void getSystemHomeDir() {
        System.out.println(PathKit.getSystemHomeDir());
    }
}