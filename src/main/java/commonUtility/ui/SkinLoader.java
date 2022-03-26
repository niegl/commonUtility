package commonUtility.ui;

import javafx.fxml.FXMLLoader;
import org.javatuples.Pair;

import java.io.IOException;

public class SkinLoader {
    /**
     * 返回loader，因为在某种特殊场景下需要通过loader.getNamespace().get的方式获取控件对象
     * @param fxml
     * @param controller
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> Pair<FXMLLoader, T> loadFxml(String fxml, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(SkinLoader.class.getResource(fxml));
        loader.setController(controller);
        return Pair.with(loader,loader.load());
    }
}
