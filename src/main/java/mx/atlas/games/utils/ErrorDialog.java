package mx.atlas.games.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.util.Arrays;

/**
 * @author finsther
 */
public class ErrorDialog {

    private static final String TITLE = "Ha ocurrido un problema";
    private static final String HEADER = "Mamma Mia!, tenemos un problema.\nComuníquese con un Administrador de Sistemas";
    private static final int MAX_CONTENT_LENGHT = 320;

    private ErrorDialog() {
    }

    public static void errorDialog(Throwable e) {
        Label label = new Label("Traza de error:");

        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(TITLE);
        alert.setHeaderText(HEADER);
        alert.setContentText(getContentText(e));
        alert.initStyle(StageStyle.UTILITY);
        alert.initModality(Modality.APPLICATION_MODAL);

        TextArea textArea = new TextArea(Arrays.toString(e.getStackTrace()));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
//        textArea.getStyleClass().add("text-area-error");

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane gp = new GridPane();
        gp.setMaxWidth(Double.MAX_VALUE);
        gp.add(label, 0, 0);
        gp.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(gp);
        alert.showAndWait();
    }

    private static String getContentText(Throwable e) {
        String content = e.getMessage();

        if (content != null && content.length() > MAX_CONTENT_LENGHT) {
            return content.substring(0, MAX_CONTENT_LENGHT).concat("... (demasiadas lineas más)");
        } else {
            return "";
        }
    }
}
