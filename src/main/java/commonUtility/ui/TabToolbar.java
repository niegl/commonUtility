package commonUtility.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Example of a horizontal ToolBar with eight buttons separated with two vertical separators.
 *  ToolBar toolBar = new ToolBar(
 *        new Button("New"),
 *        new Button("Open"),
 *        new Button("Save"),
 *        new Separator(),
 *        new Button("Clean"),
 *        new Button("Compile"),
 *        new Button("Run"),
 *        new Separator(),
 *        new Button("Debug"),
 *        new Button("Profile")
 *    );
 */
public class TabToolbar extends HBox {
    private final ObservableMap<Label, AnchorPane> items = FXCollections.observableHashMap();
    /**
     * 上部工具栏：标签页占位
      */
    Pane ttb_upTabNamePlaceholder;
    /**
     * 上部工具栏：标签页container
     */
    VBox ttb_upTabGroup;
    /**
     * 下部工具栏：标签页占位
     */
    Pane ttb_downTabNamePlaceholder;
    /**
     * 下部工具栏：标签页container
     */
    VBox ttb_downTabGroup;
    /**
     * header部分:标签页名称
     */
    Label ttb_tabName;
    /**
     * 工具栏：树形列表部分<p>
     * 标识当前需要显示哪个工具栏
     */
    Label currentToolbar;
    /**
     * 工具栏的位置（不同工具栏的拉伸宽度不同）
     */
    Map<Label, Double> toolbarPositions= new HashMap<>();
    /**
     * 工具栏显示/隐藏 部分
     */
    AnchorPane ttb_splitLeft;
    AnchorPane ttb_treePlaceholder;
    /**
     * 客户自定义部分
     */
    AnchorPane ttb_customContent;
    SplitPane splitPane;


    public TabToolbar() {
        initialize();
    }

    private void initialize() {
        // 工具栏：标签页部分
        VBox vBox = new VBox();
        HBox.setHgrow(vBox, Priority.NEVER);
        // 标签页部分：上部标签页
        ttb_upTabNamePlaceholder = new Pane();
        VBox.setVgrow(ttb_upTabNamePlaceholder,Priority.ALWAYS);
        ttb_upTabGroup = new VBox(ttb_upTabNamePlaceholder);
        VBox.setVgrow(ttb_upTabGroup,Priority.ALWAYS);
        vBox.getChildren().add(ttb_upTabGroup);
        // 标签页部分：下部标签页
        ttb_downTabNamePlaceholder = new Pane();
        VBox.setVgrow(ttb_downTabNamePlaceholder,Priority.ALWAYS);
        ttb_downTabGroup = new VBox(ttb_downTabNamePlaceholder);
        VBox.setVgrow(ttb_downTabGroup,Priority.ALWAYS);
        vBox.getChildren().add(ttb_downTabGroup);

        // 工具栏：标签内容=标签头+标签体
        // 工具栏：标签内容：标签头
        ttb_tabName = new Label();
        AnchorPane HeaderPlaceHolder = new AnchorPane();
        AnchorPane.setLeftAnchor(HeaderPlaceHolder,60d);
        AnchorPane.setRightAnchor(HeaderPlaceHolder,18d);
        // 工具栏：标签内容：标签头:最小化
        ImageView imageMinimize = new ImageView(new Image(Objects.requireNonNull(TabToolbar.class.getResourceAsStream(
                "/commonUtility/最小化.png")), 12, 12, true, true));
        Label labelMinimize = new Label();
        AnchorPane.setRightAnchor(labelMinimize,0d);
        AnchorPane.setTopAnchor(labelMinimize,0d);
        labelMinimize.setGraphic(imageMinimize);
        // 工具栏：标签内容：标签头:最大化
//        ImageView imageMaximize = new ImageView(new Image(Objects.requireNonNull(TabToolbar.class.getResourceAsStream(
//                "/images/最大化.png")), 12, 12, true, true));
//        Label labelMaximize = new Label();
//        AnchorPane.setRightAnchor(labelMaximize,0d);
//        AnchorPane.setTopAnchor(labelMaximize,0d);
//        labelMaximize.setGraphic(imageMaximize);

        AnchorPane anchorPaneHeader = new AnchorPane(ttb_tabName,HeaderPlaceHolder,labelMinimize/*,labelMaximize*/);
        anchorPaneHeader.setPrefHeight(16);
        VBox.setVgrow(anchorPaneHeader,Priority.NEVER);

        // 工具栏：标签内容：标签体
        ttb_treePlaceholder = new AnchorPane();
        VBox.setVgrow(ttb_treePlaceholder,Priority.ALWAYS);

        VBox vBoxBar = new VBox(anchorPaneHeader,ttb_treePlaceholder);
        AnchorPane.setLeftAnchor(vBoxBar,0d);
        AnchorPane.setRightAnchor(vBoxBar,0d);
        AnchorPane.setTopAnchor(vBoxBar,0d);
        AnchorPane.setBottomAnchor(vBoxBar,0d);


        ttb_splitLeft = new AnchorPane(vBoxBar);
        // 在窗体大小变化时，splitPane不随着变化。只响应鼠标拖动的大小变化
        SplitPane.setResizableWithParent(ttb_splitLeft, Boolean.FALSE);
        SplitPane.setResizableWithParent(ttb_splitLeft, Boolean.FALSE);
        ttb_customContent = new AnchorPane();
        splitPane = new SplitPane(ttb_splitLeft, ttb_customContent);

        HBox.setHgrow(splitPane,Priority.ALWAYS);
        splitPane.setDividerPositions(0.3d);

        this.getChildren().add(vBox);
        this.getChildren().add(splitPane);

        labelMinimize.setOnMouseClicked(event -> {
            AnchorPane anchorPane = items.get(currentToolbar);
            onLabelMouseClicked(anchorPane, currentToolbar);
        });

        final ObjectProperty<Boolean> maximizeProperty = new SimpleObjectProperty<>(false);

    }

    /**
     * 添加tab页
     * @param label tab页名称
     * @param item tab页内容
     */
    public void addToolbar(Label label, AnchorPane item) {
        // 添加标题
        label.setPrefWidth(20);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);
        VBox.setVgrow(label,Priority.NEVER);
        ObservableList<Node> children = ttb_upTabGroup.getChildren();
        // holder作为本组tab更改到中间位置的响应控件
        Pane holder = new Pane();
        holder.setPrefHeight(4d);
        children.add(children.size()-1, holder);
        children.add(children.size()-1, label);

        label.setOnMouseClicked(event -> {
            onLabelMouseClicked(item, label);
        });

        // 其实默认宽度都是0.3
        toolbarPositions.put(label,0.3d);
        this.items.put(label,item);

        // 默认显示第一个标签页
        if (currentToolbar == null) {
            onLabelMouseClicked(item, label);
        }
    }

    public void addBody(Node item) {
        ttb_customContent.getChildren().add(item);
        AnchorPane.setLeftAnchor(item,0d);
        AnchorPane.setRightAnchor(item,0d);
        AnchorPane.setTopAnchor(item,0d);
        AnchorPane.setBottomAnchor(item,0d);
    }

        /**
         * 点击标签响应事件
         * @param item
         * @param label
         */
    private void onLabelMouseClicked(AnchorPane item, Label label) {
        // 保存当前位置
        double dividerPosition = 0;
        if (splitPane.getDividerPositions().length != 0) {
            dividerPosition = splitPane.getDividerPositions()[0];
            // 有位置，但是显示任何toolbar，说明是第一次显示
            if (currentToolbar == null) {
                toolbarPositions.put(label, dividerPosition);
            }
        }

        String style2 = label.getStyle();
        // 删除工具栏
        splitPane.getItems().remove(ttb_splitLeft);

        // 如果当前 没有显示工具栏||显示工具栏不是label,那么显示label对应工具栏
        if (currentToolbar == null || !currentToolbar.equals(label)) {
            // 如果发生了切换，保存前面的位置设置
            if (currentToolbar != null &&!currentToolbar.equals(label)) {
                toolbarPositions.put(currentToolbar, dividerPosition);
            }
            ttb_treePlaceholder.getChildren().clear();
            ttb_treePlaceholder.getChildren().add(item);
            splitPane.getItems().add(0, ttb_splitLeft);

            AnchorPane.setLeftAnchor(item, 0d);
            AnchorPane.setRightAnchor(item, 0d);
            AnchorPane.setTopAnchor(item, 0d);
            AnchorPane.setBottomAnchor(item, 0d);
            currentToolbar = label;
            Double aDouble = toolbarPositions.get(currentToolbar);
            splitPane.setDividerPositions(aDouble);

            ttb_tabName.setText(label.getText());
        }
        // 如果当前显示工具栏就是label，那么隐藏工具栏
        else {
            toolbarPositions.put(label, dividerPosition);
            splitPane.setDividerPositions(0d);
            currentToolbar = null;
        }

        //变颜色
        items.keySet().forEach(label1 -> {
            if (currentToolbar != null && currentToolbar.equals(label1)) {
                String style = currentToolbar.getStyle();
                if (style.isEmpty()) {
                    currentToolbar.setStyle("-fx-background-color: #237ea9;");
                } else {
                    currentToolbar.setStyle("");
                }
            } else {
                label1.setStyle("");
            }
        });

    }

    /**
     * The items contained in the {@code ToolBar}. Typical use case for a
     * {@code ToolBar} suggest that the most common items to place within it
     * are {@link Button Buttons}, {@link ToggleButton ToggleButtons}, and  {@link Separator Separators},
     * but you are not restricted to just these, and can insert any {@link Node}.
     * The items added must not be null.
     * @return the list of items
     */
    public final ObservableMap<Label, AnchorPane> getItems() { return items; }




}

/**
 *     private ObjectProperty<Orientation> orientation;
 *     public final void setOrientation(Orientation value) {
 *         orientationProperty().set(value);
 *     };
 *     public final Orientation getOrientation() {
 *         return orientation == null ? Orientation.HORIZONTAL : orientation.get();
 *     }
 *     public final ObjectProperty<Orientation> orientationProperty() {
 *         if (orientation == null) {
 *             orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
 *                 @Override public void invalidated() {
 *                     final boolean isVertical = (get() == Orientation.VERTICAL);
 *                     pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE,    isVertical);
 *                     pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !isVertical);
 *                 }
 *
 *                 @Override
 *                 public Object getBean() {
 *                     return TabToolbar.this;
 *                 }
 *
 *                 @Override
 *                 public String getName() {
 *                     return "orientation";
 *                 }
 *
 *                 @Override
 *                 public CssMetaData<TabToolbar,Orientation> getCssMetaData() {
 *                     return StyleableProperties.ORIENTATION;
 *                 }
 *             };
 *         }
 *         return orientation;
 *     }
 *
 *     private static class StyleableProperties {
 *         private static final CssMetaData<TabToolbar,Orientation> ORIENTATION =
 *                 new CssMetaData<TabToolbar,Orientation>("-fx-orientation",
 *                         new EnumConverter<Orientation>(Orientation.class),
 *                         Orientation.HORIZONTAL) {
 *
 *                     @Override
 *                     public Orientation getInitialValue(TabToolbar node) {
 *                         // A vertical ToolBar should remain vertical
 *                         return node.getOrientation();
 *                     }
 *
 *                     @Override
 *                     public boolean isSettable(TabToolbar n) {
 *                         return n.orientation == null || !n.orientation.isBound();
 *                     }
 *
 *                     @Override
 *                     public StyleableProperty<Orientation> getStyleableProperty(TabToolbar n) {
 *                         return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
 *                     }
 *                 };
 *
 *         private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
 *         static {
 *             final List<CssMetaData<? extends Styleable, ?>> styleables =
 *                     new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
 *             styleables.add(ORIENTATION);
 *             STYLEABLES = Collections.unmodifiableList(styleables);
 *         }
 *     }
 *
 *     private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
 *     private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
 */