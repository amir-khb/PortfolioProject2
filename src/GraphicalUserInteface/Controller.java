package GraphicalUserInteface;

public class Controller {
	private Model m;
	private View v;
	Controller(Model m, View v){
		this.m = m;
		this.v = v;
	}
	void searchVoyagesController(String fromPortValue, String toPortValue, int depDateText, int containerValue){
		String result = m.searchVoyages(fromPortValue,  toPortValue,  depDateText,  containerValue);
		v.setOutputText(result);
	}
	void bookVoyageController(int containerNum, String customerName){
		String result = m.bookVoyage(containerNum, customerName);
		v.setOutputText(result);
	}
}
