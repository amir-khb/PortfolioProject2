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
	private Text outputText;
	private DBConnection db = new DBConnection();

	public static void main(String[] args) throws FileNotFoundException {
		TableCreation a = new TableCreation();
		a.createTables();
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

		// Initialize buttons
		Button button1 = new Button("Search for Voyages");
		button1.setOnAction(e -> searchButton());

		Button button2 = new Button("Book Voyage");
		button2.setOnAction(e -> bookButton());

		// Initialize output text field
		outputText = new Text();
		outputText.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

		// Create grid pane
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20, 20, 20, 20));
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		// Add components to grid pane
		gridPane.add(new Label("From port:"), 0, 0);
		gridPane.add(fromPort, 1, 0);
		gridPane.add(new Label("To port:"), 0, 1);
		gridPane.add(toPort, 1, 1);
		gridPane.add(new Label("Date:"), 0, 2);
		gridPane.add(depDate, 1, 2);
		gridPane.add(new Label("Containers:"), 0, 3);
		gridPane.add(numContainers, 1, 3);
		gridPane.add(button1, 0, 4);
		gridPane.add(button2, 1, 4);

		// Create vertical box
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(20, 20, 20, 20));
		vBox.setSpacing(10);

		// Add components to vertical box
		vBox.getChildren().addAll(gridPane, outputText);

		// Create scene
		Scene scene = new Scene(vBox, 400, 300);

		// Set stage properties
		primaryStage.setTitle("Portfolio Project 2");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void searchButton() {
		String[] result = searchHelper();
		if (result == null) {
			outputText.setText("No Voyage Found!");
		} else {
			outputText.setText(result[0]);
		}
	}

	private void bookButton() {
		String[] result = searchHelper();
		if (result == null) {
			outputText.setText("No Voyage Found!");
		} else {
			//Query to insert shipment information
			String query = "INSERT INTO shipment (voyage, volume, customer) " +
					"VALUES( " + result[1] + ", " + Integer.parseInt(numContainers.getText()) + ", 'Customer')";
			db.cmd(query);
			//Query to get the capacity of the vessel in order to update the capacity
			String query2 = "SELECT vs.capacity FROM vessel vs, voyage v " +
					"WHERE vs.name = v.vessel AND v.id = " + result[1];
			int capacity = Integer.parseInt(db.query(query2, "capacity").get(0));
			int remainingCapacity = capacity - Integer.parseInt(numContainers.getText());
			//Query to update the capacity of the vessel
			String query3 = "UPDATE vessel SET capacity = " + remainingCapacity + " WHERE name = '" + result[0] + "'";
			db.cmd(query3);
		}
	}

	private String[] searchHelper() {
		String[] s = new String[2];
		//Get the input from user to the code
		String fromPortValue = fromPort.getValue();
		int depDateText = Integer.parseInt(depDate.getText());
		String toPortValue = toPort.getValue();
		int containerValue = Integer.parseInt(numContainers.getText());

		String query = "SELECT v.vessel, v.id " +
				"FROM voyage v, vessel vs " +
				"WHERE v.depport = '" + fromPortValue + "' AND v.arrport = '" + toPortValue + "' " +
				"AND (v.vessel=vs.name AND vs.capacity >= " + containerValue + ") " +
				"AND v.depdate >= " + depDateText + " GROUP BY depdate";

		ArrayList<String> resultVessel = db.query(query, "vessel");
		ArrayList<String> resultId = db.query(query, "id");
		if (resultVessel.isEmpty()) {
			return null;
		} else {
			s[0] = resultVessel.get(0);
			s[1] = resultId.get(0);
			return s;
		}
	}
}
