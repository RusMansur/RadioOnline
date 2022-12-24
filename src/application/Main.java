package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class Main extends Application {
    private double xOffset;
    private double yOffset;

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
// Значок приложения в док-панели macOS
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();

            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(getClass().getResource("/resources/images/Radio.png"));
                taskbar.setIconImage(dockIcon);
            }
        }
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/mainWindow.fxml"));

        VBox root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);
        root.setOnMousePressed(event -> { // получение координат при нажатии мыши на окне
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        root.setOnMouseDragged(event -> { // установка новых координат при перетаскивании окна
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(root, Color.TRANSPARENT));
        stage.show();
    }
}
