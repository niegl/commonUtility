package commonUtility.ui;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class UILoader {
    public static <T> T loadFxml(String fxml, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(UILoader.class.getResource(fxml));
        loader.setController(controller);
        return loader.load();
    }
}
