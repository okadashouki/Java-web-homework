package com.training.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.training.action.DBConnectionFactory;
import com.training.model.Account;





public class LoginDao {
	
	private static LoginDao loginDao = new LoginDao();

	private LoginDao(){ }

	public static LoginDao getInstance(){
		return loginDao;
	}

	
	public static Account queryAccountById(String id){
		Account account = null;		
		// querySQL SQL
		String querySQL = "SELECT IDENTIFICATION_NO,CUSTOMER_NAME, PASSWORD FROM BEVERAGE_MEMBER WHERE IDENTIFICATION_NO =?";		
		// Step1:取得Connection
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
		    // Step2:Create prepareStatement For SQL
			PreparedStatement stmt = conn.prepareStatement(querySQL)){
			stmt.setString(1, id);
			try(ResultSet rs = stmt.executeQuery()){
				if(rs.next()){
					account = new Account();
					account.setId(rs.getString("IDENTIFICATION_NO"));
					account.setName(rs.getString("CUSTOMER_NAME"));
					account.setPwd(rs.getString("PASSWORD"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return account;
	}
}
