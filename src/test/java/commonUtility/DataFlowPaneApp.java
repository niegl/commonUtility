package commonUtility;

import commonUtility.ui.DataFlowPane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;

public class DataFlowPaneApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        DataFlowPane dataFlowPane = new DataFlowPane();
        Button buttton1 = new Button("buttton1");
        dataFlowPane.addEntity(buttton1,40,40);
        Button buttton2 = new Button("buttton2");
        dataFlowPane.addEntity(buttton2,200,40);

        ArrayList<Point> objects = new ArrayList<>();
        objects.add(new Point(100,40));
        objects.add(new Point(200,40));

        Label buttton3 = new Label("buttton3");
        dataFlowPane.addEntity(buttton3,140,140);
        Label buttton4 = new Label("buttton4");
        dataFlowPane.addEntity(buttton4,300,140);

        Button buttton5 = new Button("buttton5");
        dataFlowPane.addEntity(buttton5,600,400);

        HBox hbox = new HBox();
        hbox.getChildren().add(dataFlowPane);
        HBox.setHgrow(dataFlowPane, Priority.ALWAYS);
        Button button = new Button("relation");
        Button button2 = new Button("relation2");
        hbox.getChildren().addAll(button,button2);
        button.setOnMouseClicked(event -> {
            dataFlowPane.addToGroup("group1", buttton2, buttton1);
            dataFlowPane.addToGroup("group2", buttton3, buttton4);
        });
        button2.setOnMouseClicked(event -> {
            dataFlowPane.addEntityRelation(buttton1,buttton2, "label");
            dataFlowPane.addEntityRelation(buttton3,buttton4, "label");
            dataFlowPane.addEntityRelation(buttton1,buttton5, "label5");
        });
        // from =>w WW .yi  I BA I.C O M
        AnchorPane parent = new AnchorPane(hbox);
        Scene scene = new Scene(parent, 800, 600, Color.rgb(0, 0, 0, 0));
        AnchorPane.setLeftAnchor(hbox,0.);
        AnchorPane.setTopAnchor(hbox,0.);
        AnchorPane.setRightAnchor(hbox,0.);
        AnchorPane.setBottomAnchor(hbox,0.);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
