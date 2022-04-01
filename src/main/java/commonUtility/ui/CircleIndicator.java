package commonUtility.ui;


import commonUtility.context.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CircleIndicator {

    @Getter
    private final ProgressIndicator progress = new ProgressIndicator();

    public Scene show() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(progress, 330, 120, Paint.valueOf("WHITE"));
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(GUIState.getStage());
        stage.setAlwaysOnTop(true);

        stage.setUserData(progress);
        progress.progressProperty().addListener((observableValue, number, t1) -> {
            if (t1.floatValue() > 0.999f) {
                stage.close();
            }
        });
        stage.show();

        return scene;
    }

    public <U> void showWithProcess(Supplier<U> supplier, Consumer<U> consumer) {
        show();
        CompletableFuture<U> uCompletableFuture = CompletableFuture.supplyAsync(supplier);
        uCompletableFuture.thenAccept((result) -> {
            Platform.runLater(()->progress.setProgress(1.0));
            consumer.accept(result);
        });
        uCompletableFuture.exceptionally((e) -> {
            Platform.runLater(()->progress.setProgress(1.0));
            e.printStackTrace();
            return null;
        });
    }
}
