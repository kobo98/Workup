package com.app2youth.hackaton.workup;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {
	private SQL(){
		
	}
	
	private static final String DB_DRIVER =     "com.mysql.jdbc.Driver";
	/*
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/workup";
	private static final String DB_USER =       "root";
	private static final String DB_PASSWORD =   "Eclipse37370";
	*/
	
	private static final String DB_CONNECTION = "jdbc:mysql://www.db4free.net:3306/workup";
	private static final String DB_USER =       "david37370";
	private static final String DB_PASSWORD =   "123456";
	
	public static Connection con=null;
	public static Statement statement = null;
	public static Statement spareStatement = null;
	
	public static void start(){
		try {
			con = getDBConnection();
			statement = con.createStatement();
			spareStatement = con.createStatement();
			initializePreparedStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(){
		try {
			lastIdStatement.close();
			
			statement.close();
			spareStatement.close();
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private static PreparedStatement lastIdStatement;
	private static PreparedStatement selectByIdStatement;
	
	private static void initializePreparedStatements() throws SQLException{
		
		lastIdStatement = con.prepareStatement("SELECT LAST_INSERT_ID();");
		selectByIdStatement = con.prepareStatement("SELECT * FROM test WHERE id = ?");
	}
	
	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

        while(dbConnection==null)
            try {
                StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,DB_PASSWORD);
                return dbConnection;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

		return dbConnection;
	}
	
	
	
	
	
	
	
	//Prepared functions
	public static int getLastID() throws SQLException{
		ResultSet rs = lastIdStatement.executeQuery();
		int id=-1;
		while(rs.next()){
			id=rs.getInt(1);
		}
		return id;
	}
	
	public static ResultSet selectById(int id) throws SQLException{
		selectByIdStatement.setInt(1, id);
		return selectByIdStatement.executeQuery();
	}
	
}
