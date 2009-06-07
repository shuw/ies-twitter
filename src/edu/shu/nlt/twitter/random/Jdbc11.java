package edu.shu.nlt.twitter.random;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Jdbc11 {
	static final String mySqlServerLocation = "127.0.0.1:3306";
	static final String mySqlUserName = "root";
	static final String mySqlUserPwd = "direwolf";

	private static Connection GetConnection(String path) throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + mySqlServerLocation + "/" + path, "root", "direwolf");
	}

	public static void CreateDB(String dbName) {
		try {

			String url = "jdbc:mysql://127.0.0.1:3306/mysql";

			Connection con = GetConnection("mysql");

			Statement stmt = con.createStatement();

			stmt.executeUpdate("CREATE DATABASE " + dbName);
			con.close();
		} catch (SQLException e) {

		}

	}

	public static void DoSomethingNew(String dbName) {

		Connection con;
		try {
			con = GetConnection("JunkDB");

			Statement stmt = con.createStatement();

			try {
				stmt.executeUpdate("DROP TABLE myTable");
			} catch (SQLException e) {
				System.out.println("No existing table to delete");
			}

			stmt.executeUpdate("CREATE TABLE myTable(test_id int," + "test_val char(15) not null)");

			stmt.executeUpdate("INSERT INTO myTable(test_id, " + "test_val) VALUES(1,'One')");
			stmt.executeUpdate("INSERT INTO myTable(test_id, " + "test_val) VALUES(2,'Two')");
			stmt.executeUpdate("INSERT INTO myTable(test_id, " + "test_val) VALUES(3,'Three')");
			stmt.executeUpdate("INSERT INTO myTable(test_id, " + "test_val) VALUES(4,'Four')");
			stmt.executeUpdate("INSERT INTO myTable(test_id, " + "test_val) VALUES(5,'Five')");

			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public static void main(String args[]) throws Exception {

		Class.forName("com.mysql.jdbc.Driver");

		CreateDB("JunkDB");
		DoSomethingNew("JunkDB");

	}
}
