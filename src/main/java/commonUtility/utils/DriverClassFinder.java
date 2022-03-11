package commonUtility.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class DriverClassFinder {
    /**
     * 从jar包中读取指定文件的内容.
     * @param path jar包文件的路径
     * @param innerFile jar包内部文件名
     * @return 文件内容
     */
    public static String readFromJarFile(String path, String innerFile) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jarFile == null) return "";

        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry jarEntry = entrys.nextElement();
            if (!jarEntry.getName().endsWith(innerFile)) continue;

            StringBuilder stringBuilder = new StringBuilder();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarEntry)));
                String line;
                while ((line = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }
        return "";
    }

}
