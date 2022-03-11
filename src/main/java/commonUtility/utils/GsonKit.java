package commonUtility.utils;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;

/**
 * gson文件读写操作.
 */
public final class GsonKit {

    /**
     * 文件写操作。
     * @param filePath
     * @param object
     * @param <T>
     * @throws IOException – if the named file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    public static <T> void writeTypedJSON(String filePath, T object) throws IOException {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(gson.toJson(object));
        }
    }

    public static <T> T readTypedJSON(String filePath, Type typeOf) throws IOException {
        Gson gson = new Gson();
        boolean exist = PathKit.fileExist(filePath);
        if (exist) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));){
                return gson.fromJson(bufferedReader,typeOf);
            }
        }

        return null;
    }
}
