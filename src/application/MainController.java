package application;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.TreeMap;

public class MainController {
	@FXML public ImageView logoImageView;
	@FXML public ListView<String> radioListView;
	@FXML public ImageView playImageView;
	@FXML public ImageView pauseImageView;
	@FXML public Pane pane;
	@FXML public ImageView expandArrowUp;
	@FXML public ImageView expandArrowDown;
	@FXML public ImageView closeImageView;
	@FXML public ImageView backwardImageView;
	@FXML public ImageView forwardImageView;
	@FXML public ImageView addRadioStationImageView;
	@FXML public TextField nameTextField;
	@FXML public TextField logoTextField;
	@FXML public TextField urlTextField;
	@FXML public Pane addRadioPane;
	@FXML public Label cancelLabel;
	@FXML public Label addChangeLabel;
	@FXML public MenuItem changeMenu;
	@FXML public MenuItem deleteMenu;
	@FXML public ImageView volumeImageView;
	@FXML public Slider volumeSlider;
	@FXML public Label titleLabel;

	RadioStationDB radioStationDB = new RadioStationDB();
	TreeMap<String, RadioStation> radioStationList = radioStationDB.readStationsFromFile();
	MultipleSelectionModel<String> selectionModel;
	MediaPlayer mediaPlayer;
	Duration duration = new Duration(500.0);
	private Stage stage;
	private int index;

	@FXML
	void initialize() {
		volumeSlider.setValue(100.0);
		radioListView.setItems(radioStationDB.nameList());
		pauseImageView.setVisible(false);
		pane.setOpacity(0.0);
		radioStationDB.createStationsListFile();
		nameTextField.textProperty().addListener((object, oldValue, newValue) -> {
			if (newValue.equals("")) {
				nameTextField.setStyle("-fx-border-color: red");
			} else {
				nameTextField.setStyle("-fx-border-color: #bebebe");
			}
			if (radioStationList.containsKey(newValue)) {
				titleLabel.setText("Изменить радиостанцию");
				addChangeLabel.setText("Изменить");
			} else {
				titleLabel.setText("Добавить радиостанцию");
				addChangeLabel.setText("Добавить");
			}
		});
		urlTextField.textProperty().addListener((object, oldValue, newValue) -> {
			if (!newValue.contains("http")) {
				urlTextField.setStyle("-fx-border-color: red");
			} else {
				urlTextField.setStyle("-fx-border-color: #bebebe");
			}
		});
	}

	// Управление воспроизведением
	public void onPlayButtonClick(MouseEvent mouseEvent) {
		selectionModel = radioListView.getSelectionModel();
		String name = selectionModel.getSelectedItem();
		RadioStation radioStation = radioStationDB.getRadioStation(name);
		if (radioStation == null) {
			radioStation = new RadioStation(
					radioStationList.firstKey(),
					radioStationList.firstEntry().getValue().getLogoPath(),
					radioStationList.firstEntry().getValue().getUrl()
			);
		}
		if (!playRadioStation(radioStation)) onForwardButtonClick(mouseEvent);
	}

	private boolean playRadioStation(RadioStation radioStation) {
		if (mediaPlayer != null) mediaPlayer.stop();
		try {
			mediaPlayer = new MediaPlayer(new Media(radioStation.getUrl()));
		} catch (IllegalArgumentException exception) {
			return false;
		}
		mediaPlayer.statusProperty().addListener((obj, oldValue, newValue) -> {
			if (newValue == MediaPlayer.Status.READY) {
				mediaPlayer.play();
				playImageView.setVisible(false);
				pauseImageView.setVisible(true);
			}
		});
		if (radioStation.getLogoPath().contains("http")) {
			logoImageView.setImage(new Image(radioStation.getLogoPath()));
		} else {
			logoImageView.setImage(new Image(new File(radioStation.getLogoPath()).toURI().toString()));
		}
		return true;
	}

	public void onForwardButtonClick(MouseEvent mouseEvent) {
		changeRadioStation(true);
	}

	public void onBackwardButtonClick(MouseEvent mouseEvent) {
		changeRadioStation(false);
	}

	public void onPauseImageViewClick(MouseEvent mouseEvent) {
		mediaPlayer.pause();
		playImageView.setVisible(true);
		pauseImageView.setVisible(false);
	}

	private void changeRadioStation(boolean forward) {
		selectionModel = radioListView.getSelectionModel();
		index = selectionModel.getSelectedIndex();
		if (forward) {
			if (index < radioStationList.size() - 1) {selectionModel.selectNext();} else {selectionModel.selectFirst();}
		} else {
			if (index > 0) {selectionModel.selectPrevious();} else selectionModel.selectLast();
		}
		RadioStation radioStation = radioStationList.get(selectionModel.getSelectedItem());
		playRadioStation(radioStation);
	}

	// Управление списком радиостанций
	public void onArrowUpClick(MouseEvent mouseEvent) {
		stage.setHeight(210);
		expandArrowUp.setVisible(false);
		expandArrowDown.setVisible(true);
	}

	public void onArrowDownClick(MouseEvent mouseEvent) {
		stage.setHeight(420);
		expandArrowUp.setVisible(true);
		expandArrowDown.setVisible(false);
	}

	public void onItemListViewClicked(MouseEvent mouseEvent) {
		onPlayButtonClick(mouseEvent);
	}

	// Добавление и изменение радиостанций
	public void onPlusImageViewClick(MouseEvent mouseEvent) {
		titleLabel.setText("Добавить радиостанцию");
		addChangeLabel.setText("Добавить");
		cleanTextFields();
		addChangeStationPaneShow();
	}

	public void onAddChangeLabelClick(MouseEvent mouseEvent) {
		RadioStation radioStation = new RadioStation(nameTextField.getText(), logoTextField.getText(), urlTextField.getText());
		if (radioStation.getName().length() != 0) {
			if (playRadioStation(radioStation)) {
				radioStationList.put(nameTextField.getText(), radioStation);
				if (addChangeLabel.getText().equals("Добавить")) {
					radioStationDB.saveStationToFile(radioStation);
				} else {
					radioStationDB.saveListOfStationsToFile(radioStationList);
				}
				radioListView.setItems(radioStationDB.nameList());
			}
			onCancelLabelClick(mouseEvent);
		}
	}

	public void onCancelLabelClick(MouseEvent mouseEvent) {
		addRadioPane.setVisible(false);
		radioListView.setVisible(true);
	}

	// Контекстное меню
	public void onChangeMenuClick(ActionEvent actionEvent) {
		selectionModel = radioListView.getSelectionModel();
		index = selectionModel.getSelectedIndex();
		String nameString = selectionModel.getSelectedItem();
		RadioStation radioStation = radioStationDB.getRadioStation(nameString);
		titleLabel.setText("Изменить радиостанцию");
		addChangeLabel.setText("Изменить");
		addChangeStationPaneShow();
		if (radioStation != null) {
			nameTextField.setText(radioStation.getName());
			logoTextField.setText(radioStation.getLogoPath());
			urlTextField.setText(radioStation.getUrl());
		}
	}

	public void onDeleteMenuClick(ActionEvent actionEvent) {
		selectionModel = radioListView.getSelectionModel();
		String name = selectionModel.getSelectedItem();
		radioStationList.remove(name);
		radioStationDB.saveListOfStationsToFile(radioStationList);
		radioListView.setItems(radioStationDB.nameList());
	}

	// Громкость
	public void onVolumeImageViewClick(MouseEvent mouseEvent) {
		volumeSlider.setVisible(!volumeSlider.isVisible());
	}

	public void onVolumeSliderDrag(MouseEvent mouseEvent) {
		ChangeListener<Object> listener = (observableValue, o, t1) -> {
			if (volumeSlider.isValueChanging()) {
				mediaPlayer.setVolume(volumeSlider.getValue() / 100);
			}
		};
		volumeSlider.valueProperty().addListener(listener);
		volumeSlider.valueChangingProperty().addListener(listener);
	}

	public void onVolumeSliderExited(MouseEvent mouseEvent) {
		volumeSlider.setVisible(false);
	}

	// Панель управления
	public void onPaneMouseMoved(MouseEvent mouseEvent) {
		pane.setOpacity(0.5);
	}

	public void onPaneMouseExited(MouseEvent mouseEvent) {
		FadeTransition fadeTransition = new FadeTransition(duration, pane);
		fadeTransition.setOnFinished(event -> {
			if (volumeSlider.isVisible()) volumeSlider.setVisible(false);
		});
		fadeTransition.setFromValue(0.5);
		fadeTransition.setToValue(0.0);
		fadeTransition.setDelay(duration);
		fadeTransition.play();
	}

	private void addChangeStationPaneShow() {
		radioListView.setVisible(false);
		stage.setHeight(420);
		addRadioPane.setVisible(true);
	}

	// Очистка текстовых полей
	private void cleanTextFields() {
		nameTextField.setText("");
		logoTextField.setText("");
		urlTextField.setText("");
	}

	public void setPrimaryStage(Stage stage) {
		this.stage = stage;
	}

	// Закрыть приложение
	public void onCloseImageViewClick(MouseEvent mouseEvent) {
		Platform.exit();
	}
}
