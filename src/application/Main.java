package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {

	private double xOffset;
	private double yOffset;

	public static void main(String[] args) {
		Application.launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/resources/images/Radio.png"))));
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/mainWindow.fxml"));
		VBox root = fxmlLoader.load();
		MainController controller = fxmlLoader.getController();
		controller.setPrimaryStage(stage);
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		root.setOnMousePressed(event -> {
			xOffset = stage.getX() - event.getScreenX();
			yOffset = stage.getY() - event.getScreenY();
		});
		root.setOnMouseDragged(event -> {
			stage.setX(event.getScreenX() + xOffset);
			stage.setY(event.getScreenY() + yOffset);
		});
		stage.setScene(new Scene(root));
		stage.show();
	}
}
