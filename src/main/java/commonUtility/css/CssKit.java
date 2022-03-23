package commonUtility.css;

import javafx.scene.Node;

public class CssKit {
    public static void addCss(Node node, String cssStyle) {
        node.getStyleClass().remove(cssStyle);
        node.getStyleClass().add(cssStyle);
    }

    public static void removeCss(Node node, String cssStyle) {
        node.getStyleClass().remove(cssStyle);
    }

    public static void switchCss(Node node, String cssStyle) {
        if (node.getStyleClass().contains(cssStyle)) {
            removeCss(node,cssStyle);
        } else {
            addCss(node,cssStyle);
        }
    }
}
