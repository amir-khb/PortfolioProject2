package GraphicalUserInteface;

import HelperClasses.DBConnection;
import HelperClasses.TableCreation;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Model {
	private DBConnection db;
	private String output;
	private String[] info;
	private String[] userInput;

	public String getOutput() {
		return output;
	}

	public Model() throws FileNotFoundException {
		db = new DBConnection();
		//Table Creation at the beginning of the program is done in this Model
		TableCreation a = new TableCreation();
		a.createTables();
		info = new String[4];
		userInput = new String[4];
	}

	public String searchVoyages(String fromPortValue, String toPortValue, int depDateText, int containerValue) {
		userInput[0] = fromPortValue;
		userInput[1] = toPortValue;
		userInput[2] = String.valueOf(depDateText);
		userInput[3] = String.valueOf(containerValue);
		String[] result = searchVoyagesHelper(fromPortValue, toPortValue, depDateText, containerValue);
		if (result == null) {
			output = "No Voyage Found!";
		} else {
			output = "Vessel: " + result[0] + "\nDeparture Date: " + result[2] + "\nArrival Date: " + result[3];
		}
		return output;
	}

	public String[] searchVoyagesHelper(String fromPortValue, String toPortValue, int depDateText, int containerValue) {
		if (fromPortValue.equals("Choose") || toPortValue.equals("Choose") || depDateText <= 0 || containerValue <= 0) {
			return null;
		}

		//Query to find a voyage that satisfies all the requirements of the user input2
		String query = "SELECT v.vessel, v.id, v.depdate, v.arrdate " +
				"FROM voyage v, vessel vs " +
				"WHERE v.depport = '" + fromPortValue + "' AND v.arrport = '" + toPortValue + "' " +
				"AND (v.vessel=vs.name AND vs.capacity >= " + containerValue + ") " +
				"AND v.depdate >= " + depDateText + " GROUP BY depdate";

		ArrayList<String> resultVessel = db.query(query, "vessel");
		ArrayList<String> resultId = db.query(query, "id");
		ArrayList<String> resultDepdate = db.query(query, "depdate");
		ArrayList<String> resultArrdate = db.query(query, "arrdate");


		if (resultVessel.isEmpty()) {
			info[0] = null;
			info[1] = null;
			info[2] = null;
			info[3] = null;
			return null;
		} else {
			info[0] = resultVessel.get(0);
			info[1] = resultId.get(0);
			info[2] = resultDepdate.get(0);
			info[3] = resultArrdate.get(0);
			return info;
		}
	}

	public String bookVoyage(int numContainer, String customerName) {
		if(userInput[0]==null){
			output = "You need to search first!";
			return output;
		}
		searchVoyages(userInput[0], userInput[1], Integer.parseInt(userInput[2]), numContainer);
		if (info[0] == null || info[1] == null || info[2] == null || info[3] == null) {
			output = "Cannot Book, No Voyage Found!";
		} else {
			//Query to insert shipment information
			String query = "INSERT INTO shipment (voyage, volume, customer) " +
					"VALUES( " + info[1] + ", " + numContainer + ",'" + customerName + "' )";
			db.cmd(query);
			//Query to get the capacity of the vessel in order to update the capacity
			String query2 = "SELECT vs.capacity FROM vessel vs, voyage v " +
					"WHERE vs.name = v.vessel AND v.id = " + info[1];
			int capacity = Integer.parseInt(db.query(query2, "capacity").get(0));
			int remainingCapacity = capacity - numContainer;
			//Query to update the capacity of the vessel
			String query3 = "UPDATE vessel SET capacity = " + remainingCapacity + " WHERE name = '" + info[0] + "'";
			db.cmd(query3);
			output = "Shipment was saved to the database successfully!";
		}
		return output;
	}
}
