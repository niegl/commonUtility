package commonUtility.ui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.apache.commons.lang3.function.TriFunction;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class PivotZoom {

    private static Pair<StackPane, Pane> createContent(double width, double height, TriFunction<MouseEvent, Node, Point2D, Boolean> dragCallback) {

//        final Canvas[] canvas = {createBackdrop(width, height)};
        InputStream BIAsStream = PivotZoom.class.getResourceAsStream("/背景.png");
        Pane content = new Pane();
        if (BIAsStream != null) {
            BackgroundImage BI= new BackgroundImage(new Image(BIAsStream,32,32,false,true),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            content.setBackground(new Background(BI));
        }

        StackPane mergeContent = new StackPane(/*canvas[0],*/ content);
        mergeContent.setAlignment(Pos.TOP_LEFT);

        class DragData {
            double startX;
            double startY;
            double startLayoutX;
            double startLayoutY;
            Node dragTarget;
        }

        DragData dragData = new DragData();

        content.setOnMousePressed(evt -> {
            Node n = (Node) evt.getTarget();
            if (n != content) {
                if (dragCallback != null) {
                    Boolean processed = dragCallback.apply(evt, n, new Point2D(evt.getX(), evt.getY()));
                }
                // initiate drag gesture, if a child of content receives the
                // event to prevent ScrollPane from panning.
                evt.consume();
                evt.setDragDetect(true);
            }
        });

        content.setOnDragDetected(evt -> {
            Node n = (Node) evt.getTarget();
            if (n != content) {
                // set start paremeters
                while (n.getParent() != content && n.getParent().getClass() != Group.class) {
                    n = n.getParent();
                }
                dragData.startX = evt.getX();
                dragData.startY = evt.getY();
                dragData.startLayoutX = n.getLayoutX();
                dragData.startLayoutY = n.getLayoutY();
                dragData.dragTarget = n;
                n.startFullDrag();
                evt.consume();
            }
        });

        // stop dragging when mouse is released
        content.setOnMouseReleased(evt -> {

            // 鼠标释放后，根据全部子控件边界计算整体边界，根据计算出的边界重绘背景.
            BoundingBox reduceBounding = content.getChildren().parallelStream().map(c -> {
                Bounds bounds = c.getLayoutBounds();
                double layoutX = c instanceof Circle? c.getLayoutX()-((Circle)c).getRadius():c.getLayoutX();
                double layoutY = c instanceof Circle? c.getLayoutY()-((Circle)c).getRadius():c.getLayoutY();
                return new BoundingBox(layoutX, layoutY,
                        layoutX + bounds.getWidth(), layoutY + bounds.getHeight());
            }).reduce(new BoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE), (boundingBox, boundingBox2) ->
                    new BoundingBox(Math.min(boundingBox.getMinX(), boundingBox2.getMinX()),
                            Math.min(boundingBox.getMinY(), boundingBox2.getMinY()),
                            Math.max(boundingBox.getWidth(), boundingBox2.getWidth()),
                            Math.max(boundingBox.getHeight(), boundingBox2.getHeight())
            ));

            // 考虑子对象不位于边界的情况，计算出来的宽度可能小于需要的宽度
            double reduceWidth = reduceBounding.getWidth() - min(reduceBounding.getMinX(),0);
            double reduceHeight = reduceBounding.getHeight() - min(reduceBounding.getMinY(),0) ;

            double maxWidth = max(width, reduceWidth);
            double maxHeight = max(height, reduceHeight);
            if (maxWidth != content.getWidth() || maxHeight != content.getHeight()) {
                content.setMinWidth(maxWidth + 50); // 预留50个宽度
                content.setMinHeight(maxHeight + 50);// 预留50个宽度
                content.autosize();
            }
            if (dragCallback != null) {
                double layoutX = evt.getX() + dragData.startLayoutX - dragData.startX;
                double layoutY = evt.getY() + dragData.startLayoutY - dragData.startY;
                dragCallback.apply(evt, dragData.dragTarget, new Point2D(layoutX, layoutY));
            }

            dragData.dragTarget = null;
        });

        content.setOnMouseDragged(evt -> {
            if (dragData.dragTarget != null) {
                // move dragged node
                double deltaX = evt.getX() - dragData.startX;
                double deltaY = evt.getY() - dragData.startY;
                double layoutX = dragData.startLayoutX + deltaX;
                double layoutY = dragData.startLayoutY + deltaY;
                if (dragCallback != null) {
                    Boolean processed = dragCallback.apply(evt, dragData.dragTarget, new Point2D(deltaX, deltaY));
                    // 如果已经处理过，那么就不需要在处理了
                    if (processed) {
                        evt.consume();
                        return;
                    }
                }
                dragData.dragTarget.setLayoutX(layoutX);
                dragData.dragTarget.setLayoutY(layoutY);

                evt.consume();
            }
        });

        return Pair.with(mergeContent, content);
    }

    @NotNull
    private static Canvas createBackdrop(double width, double height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, width, height);

        gc.setStroke(Color.BLUE);
        gc.beginPath();

        for (int i = 50; i < width; i += 50) {
            gc.moveTo(i, 0);
            gc.lineTo(i, height);
        }

        for (int i = 50; i < height; i += 50) {
            gc.moveTo(0, i);
            gc.lineTo(width, i);
        }
        gc.stroke();
        return canvas;
    }

    public static Pair<ScrollPane, Pane> createPivotZoom(double width, double height, TriFunction<MouseEvent, Node, Point2D, Boolean> dragCallback) {
        Pair<StackPane, Pane> pairZoom = createContent(width,height,dragCallback);
        StackPane zoomTarget = pairZoom.getValue0();
        Pane pairContent = pairZoom.getValue1();

        zoomTarget.setPrefSize(width, height);
        zoomTarget.setOnDragDetected(evt -> {
            Node target = (Node) evt.getTarget();
            while (target != zoomTarget && target != null) {
                target = target.getParent();
            }
            if (target != null) {
                target.startFullDrag();
            }
        });

        Group group = new Group(zoomTarget);
        zoomTarget.setStyle("-fx-border-color: red");
        // stackpane for centering the content, in case the ScrollPane viewport
        // is larget than zoomTarget
        StackPane content = new StackPane(group);
        group.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            // keep it at least as large as the content
            content.setMinWidth(newBounds.getWidth());
            content.setMinHeight(newBounds.getHeight());
        });

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPannable(true);
        scrollPane.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            // use vieport size, if not too small for zoomTarget
            content.setPrefSize(newBounds.getWidth(), newBounds.getHeight());
        });

        content.setOnScroll(evt -> {
            if (evt.isControlDown()) {
                evt.consume();

                final double zoomFactor = evt.getDeltaY() > 0 ? 1.05 : 1 / 1.05;

                Bounds groupBounds = group.getLayoutBounds();
                final Bounds viewportBounds = scrollPane.getViewportBounds();

                // calculate pixel offsets from [0, 1] range
                double valX = scrollPane.getHvalue() * (groupBounds.getWidth() - viewportBounds.getWidth());
                double valY = scrollPane.getVvalue() * (groupBounds.getHeight() - viewportBounds.getHeight());

                // convert content coordinates to zoomTarget coordinates
                Point2D posInZoomTarget = zoomTarget.parentToLocal(group.parentToLocal(new Point2D(evt.getX(), evt.getY())));

                // calculate adjustment of scroll position (pixels)
                Point2D adjustment = zoomTarget.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

                // do the resizing
                zoomTarget.setScaleX(zoomFactor * zoomTarget.getScaleX());
                zoomTarget.setScaleY(zoomFactor * zoomTarget.getScaleY());

                // refresh ScrollPane scroll positions & content bounds
                scrollPane.layout();

                // convert back to [0, 1] range
                // (too large/small values are automatically corrected by ScrollPane)
                groupBounds = group.getLayoutBounds();
                scrollPane.setHvalue((valX + adjustment.getX()) / (groupBounds.getWidth() - viewportBounds.getWidth()));
                scrollPane.setVvalue((valY + adjustment.getY()) / (groupBounds.getHeight() - viewportBounds.getHeight()));
            }
        });
        return Pair.with(scrollPane,pairContent);
    }

}

