package GraphicalUserInteface;

import HelperClasses.DBConnection;
import HelperClasses.TableCreation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class View extends Application {
	private ComboBox<String> fromPort;
	private ComboBox<String> toPort;
	private TextField depDate;
	private TextField numContainers;
	private TextField customerName;
	private Text outputText;
	private Model m = new Model();

	private Controller c = new Controller(m, this);
	private DBConnection db = new DBConnection();

	public void setOutputText(String s) {
		outputText.setText(s);
	}

	public View() throws FileNotFoundException {
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		// Initialize combo boxes
		String queryGetPorts = "select * from port";
		ArrayList<String> res = db.query(queryGetPorts, "name");
		fromPort = new ComboBox<>();
		toPort = new ComboBox<>();
		for (String s : res) {
			fromPort.getItems().add(s);
			toPort.getItems().add(s);
		}
		fromPort.setValue("Choose");
		toPort.setValue("Choose");

		// Initialize text fields
		depDate = new TextField();
		numContainers = new TextField();
		customerName = new TextField();

		// Initialize buttons
		Button button1 = new Button("Search for Voyages");
		button1.setOnAction(e -> c.searchVoyagesController(fromPort.getValue(), toPort.getValue(), (depDate.getText().equals("")) ? 0 : Integer.parseInt(depDate.getText()), (numContainers.getText().equals("")) ? 0 : Integer.parseInt(numContainers.getText())));

		//One line if statements are used in both of the button actions when sending values to controller to ensure the input is valid and if it is not an error will be thrown in the user interface

		Button button2 = new Button("Book Voyage");
		button2.setOnAction(e -> c.bookVoyageController((numContainers.getText().equals("")) ? 0 : Integer.parseInt(numContainers.getText()), customerName.getText()));

		// Initialize output text field
		outputText = new Text();
		outputText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

		// Create grid pane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		// Add components to grid pane
		gridPane.add(new Label("Customer Name:"), 0, 0);
		gridPane.add(customerName, 1, 0);
		gridPane.add(new Label("From port:"), 0, 1);
		gridPane.add(fromPort, 1, 1);
		gridPane.add(new Label("To port:"), 0, 2);
		gridPane.add(toPort, 1, 2);
		gridPane.add(new Label("Date:"), 0, 3);
		gridPane.add(depDate, 1, 3);
		gridPane.add(new Label("Containers:"), 0, 4);
		gridPane.add(numContainers, 1, 4);
		gridPane.add(button1, 0, 5);
		gridPane.add(button2, 1, 5);

		// Create vertical box
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(20, 20, 20, 20));
		vBox.setSpacing(10);

		// Add components to vertical box
		vBox.getChildren().addAll(gridPane, outputText);

		// Create scene
		Scene scene = new Scene(vBox, 400, 350);

		// Set stage properties
		primaryStage.setTitle("Portfolio Project 2");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}