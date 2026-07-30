package com.achraf.textor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Entry point for the Textor notepad application.
 *
 * <p>Launch via Maven: {@code mvn javafx:run}
 * <p>Or run the packaged jar: {@code java -jar textor-1.1.0.jar}
 */
public final class Textor extends Application {

    private static final String APP_NAME  = "Textor";
    private static final String APP_ICON  = "/img/icon.png";
    private static final String FXML_PATH = "/ui/textor.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(Textor.class.getResource(FXML_PATH),
                        "Missing FXML resource: " + FXML_PATH));
        Parent root = loader.load();

        TextorController controller = loader.getController();
        controller.bindStage(stage);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(Textor.class.getResource("/ui/textor.css"),
                        "Missing stylesheet").toExternalForm());

        stage.setTitle(APP_NAME);
        stage.getIcons().add(new Image(APP_ICON));
        stage.setScene(scene);
        stage.setMinWidth(420);
        stage.setMinHeight(320);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
