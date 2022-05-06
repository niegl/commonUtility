package commonUtility.css;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public class CssKit {
    public static void addCss(Node node, String cssStyle) {
//        removeCss(node,cssStyle);
        if (!node.getStyleClass().contains(cssStyle)) {
            node.getStyleClass().add(cssStyle);
        }
    }

    public static void removeCss(Node node, String cssStyle) {
        ObservableList<String> styleClass = node.getStyleClass();
        styleClass.remove(cssStyle);
    }

    synchronized public static void switchCss(Node node, String cssStyle) {
        if (node.getStyleClass().contains(cssStyle)) {
            removeCss(node,cssStyle);
        } else {
            addCss(node,cssStyle);
        }
    }
}
