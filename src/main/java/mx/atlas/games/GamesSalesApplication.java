package mx.atlas.games;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mx.atlas.games.controllers.GameSaleController;
import mx.atlas.games.db.Connection;
import mx.atlas.games.utils.ErrorDialog;

import java.io.IOException;
import java.util.Objects;

public class GamesSalesApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Connection.load();
        } catch (Throwable ex) {
            System.err.println("Unable to connect to the MongoDB instance due to an error: " + ex);
            ErrorDialog.errorDialog(ex);
            ex.printStackTrace();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-sale-view.fxml"));
        Parent load = fxmlLoader.load();
        GameSaleController controller = fxmlLoader.getController();
        controller.init();
        Scene scene = new Scene(load);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("fontStyles.css")).toExternalForm());

        stage.setTitle("Games Sales");
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon/img.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}