module RadioOnline {
	opens application to javafx.graphics, javafx.controls, javafx.fxml;
	requires javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.media;
	requires com.opencsv;
    requires lombok;
	requires java.desktop;
}