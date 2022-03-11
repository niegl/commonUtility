package commonUtility.context;

import javafx.stage.Stage;

/**
 * 目前GUIState的赋值是在窗体focus事件中进行的。所以stage必须要在窗体显示完成以后才有效。
 */
public enum GUIState {
    INSTANCE;

    private static Stage stage;

    private GUIState() {
    }

    public static Stage getStage() {
        return stage;
    }

    synchronized public static void setStage(Stage stage) {
        GUIState.stage = stage;
    }
}
