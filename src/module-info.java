module Chess {
	requires javafx.controls;
	requires javafx.graphics;
	
	opens ch.david.application to javafx.graphics, javafx.fxml;
}
