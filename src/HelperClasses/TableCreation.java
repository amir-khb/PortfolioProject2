package HelperClasses;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;

public class TableCreation {
	public static void createTables() throws FileNotFoundException {
		DBConnection db = new DBConnection();
		String dropAllTablesQuery =
				"drop table if exists shipment;\n" +
						"drop table if exists voyage;\n" +
						"drop table if exists port;\n" +
						"drop table if exists vessel;";

		String creteTablesQuery =
				"create table vessel(name text primary key,\n" +
						"capacity integer);\n" +
						"create table port(name text primary key);\n" +
						"create table voyage(\n" +
						"id integer primary key autoincrement,\n" +
						"depdate integer, arrdate integer,\n" +
						"vessel Text references vessel(name),\n" +
						"depport text references port(name),\n" +
						"arrport text references port(name) );\n" +
						"create table shipment(\n" +
						"id integer primary key autoincrement,\n" +
						"voyage id references voyage(id),\n" +
						"volume integer,\n" +
						"customer text );";
		db.cmd(dropAllTablesQuery);
		db.cmd(creteTablesQuery);

		HashSet<String> vessel = new HashSet<>();
		HashSet<String> port = new HashSet<>();
		BufferedReader in = new BufferedReader(new FileReader("routes.csv"));
		try {
			while (true) {
				String s = in.readLine();
				if (s == null) break;
				String sTempQuery = "insert into voyage (depdate,arrdate,vessel,depport,arrport) values (" + s + ");";
				db.cmd(sTempQuery);
				String a[] = s.split(",");
				vessel.add(a[2]);
				port.add(a[3]);
				port.add(a[4]);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		for (String s : vessel) {
			String queryTemp = "insert into vessel values(" + s + ",3500);";
			db.cmd(queryTemp);
		}
		for (String s : port) {
			String queryTemp = "insert into port values(" + s + ");";
			db.cmd(queryTemp);
		}
		try {
			while (true) {
				String s = in.readLine();
				if (s == null) break;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}