package commonUtility.ui;

import javafx.scene.control.TreeItem;

public class Dummy {
    public static <T> TreeItem<T> newTreeItem(T name) {
        return new TreeItem<>(name);
    }
}
