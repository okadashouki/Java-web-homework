package com.training.dao;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.training.action.DBConnectionFactory;
import com.training.model.Goods;
import com.training.model.SalesReport;
import com.training.model.Select;

public class BackendDao {

	private static BackendDao backenddao = new BackendDao();
	private BackendDao(){ }

	public static BackendDao getInstance() {
	
		return backenddao;
	}
   //找單個商品
	public static Goods shohinsagasu(String id) {
		Goods good = new Goods();
		String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE goods_ID=?";
		Connection conn = DBConnectionFactory.getOracleDBConnection();
		try(PreparedStatement stmt = conn.prepareStatement(querySQL)){
			stmt.setString(1,id);
				ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				good.setGoodsID(rs.getString("goods_ID"));
				good.setGoodsName(rs.getString("goods_Name"));
				good.setGoodsPrice(rs.getInt("Price"));
				good.setGoodsQuantity(rs.getInt("Quantity"));
				good.setGoodsImageName(rs.getString("Image_Name"));
				good.setStatus(rs.getString("status"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		return good;
	}

	public boolean updateGoods(Goods goods) {
		boolean updateSuccess = false;
		int updateCount = 0;

		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			conn.setAutoCommit(false);
			String updateSql = "UPDATE BEVERAGE_GOODS SET goods_Name =?,Price=?, ";
			updateSql += "Quantity = ?,Image_Name=?,status = ? WHERE goods_ID=?";

			try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
				stmt.setString(1, goods.getGoodsName());
				stmt.setInt(2, goods.getGoodsPrice());
				stmt.setInt(3, goods.getGoodsQuantity());
				stmt.setString(4, goods.getGoodsImageName());
				stmt.setString(5, goods.getStatus());
				stmt.setString(6, goods.getGoodsID());

				updateCount = stmt.executeUpdate();
				if(updateCount>0){
					conn.commit();
				}else{
					conn.rollback();
				}
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		updateSuccess = updateCount > 0 ? true : false;
		return updateSuccess;
	}

	public int createGoods(Goods goods) {
		int goodsID = 0;
		String[] cols = { "goods_ID" };
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			conn.setAutoCommit(false);
			String insertSQL = "INSERT INTO BEVERAGE_GOODS (goods_ID,goods_Name,Price,Quantity,Image_Name,status)";
					insertSQL += " VALUES (BEVERAGE_GOODS_SEQ.NEXTVAL,?,?,?,?,?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertSQL,
					cols)) {
				// pstmt.setBigDecimal(1, goods.getGoodsID());
				pstmt.setString(1, goods.getGoodsName());
				pstmt.setInt(2, goods.getGoodsPrice());
				pstmt.setInt(3, goods.getGoodsQuantity());
				pstmt.setString(4, goods.getGoodsImageName());
				pstmt.setString(5, goods.getStatus());
				pstmt.executeUpdate();
				ResultSet rsKeys = pstmt.getGeneratedKeys();
				rsKeys.next();
				goodsID = rsKeys.getInt(1);
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
				
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return goodsID;
	}

	public Set<SalesReport> queryOrderBetweenDate(String startdate,
			String enddate) {
		Set<SalesReport> reports = new LinkedHashSet<>();

		String querySQL = " WITH G AS(  SELECT O.CUSTOMER_ID,O.ORDER_DATE,O.GOODS_ID,";
		querySQL += " O.ORDER_ID,O.GOODS_BUY_PRICE,O.BUY_QUANTITY, M.CUSTOMER_NAME, B.GOODS_NAME,";
		querySQL += " (O.GOODS_BUY_PRICE*O.BUY_QUANTITY)buyAmount";
		querySQL += " FROM BEVERAGE_ORDER O JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO";
		querySQL += " JOIN BEVERAGE_GOODS B ON O.GOODS_ID = B.GOODS_ID)";
		querySQL += " SELECT ORDER_ID,CUSTOMER_NAME,ORDER_DATE,GOODS_NAME,GOODS_BUY_PRICE,";
		querySQL += " BUY_QUANTITY,buyAmount FROM G";
		querySQL += " WHERE ORDER_DATE BETWEEN ? AND ? ORDER BY ORDER_ID";
		Connection conn = DBConnectionFactory.getOracleDBConnection();
		try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {

			stmt.setDate(1, new Date(parseDate(startdate).getTime()));
			stmt.setDate(2, new Date(parseDate2(enddate).getTime() + 24
					* 60 * 60 * 1000 - 1));
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {

				SalesReport report = new SalesReport();

				report.setOrderID(rs.getLong("ORDER_ID"));
				report.setCustomerName(rs.getString("CUSTOMER_NAME"));
				report.setOrderDate(rs.getString("ORDER_DATE"));
				report.setGoodsName(rs.getString("GOODS_NAME"));
				report.setGoodsBuyPrice(rs.getInt("GOODS_BUY_PRICE"));
				report.setBuyQuantity(rs.getInt("BUY_QUANTITY"));
				report.setBuyAmount(rs.getInt("BUYAMOUNT"));
				reports.add(report);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		return reports;
	}
	private static java.util.Date parseDate2(String enddate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date udHiredate = null;
		try {
			udHiredate = simpleDateFormat.parse(enddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time = udHiredate.getTime() + 24 * 60 * 60 * 1000 - 1;
		java.sql.Date sdHiredate = new java.sql.Date(time);
		return sdHiredate;
	}

	private static java.util.Date parseDate(String startdate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date udHiredate = null;
		try {
			udHiredate = simpleDateFormat.parse(startdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time = udHiredate.getTime();
		java.sql.Date sdHiredate = new java.sql.Date(time);
		return sdHiredate;
	}

	
	//下一步 找全部 karamade輸入0 兩個同時等於0就不輸入(等於全部商品)
	public List<Goods> selectGoods(Select select, int made, int kara) {
		List<Goods> Goods = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		ArrayList<Object> list= new ArrayList<Object>();
		sql.append("SELECT * FROM(SELECT ROWNUM ROW_NUM, S.* FROM ");
		sql.append(" (SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID IS NOT NULL");
		
		Connection conn = DBConnectionFactory.getOracleDBConnection();
	
		try {	
			if(select.getGoodsID()!=null&&!"".equals(select.getGoodsID())&&!"0".equals(select.getGoodsID())){
				sql.append(" AND GOODS_ID = ?");
				list.add(Integer.parseInt(select.getGoodsID()));
			}
			if(select.getminPrice()!=null&&!"".equals(select.getminPrice())){
				sql.append(" AND PRICE >= ?");
				list.add(Integer.parseInt(select.getminPrice()));
			}
			if(select.getmaxPrice()!=null&&!"".equals(select.getmaxPrice())){
				sql.append(" AND PRICE <= ?");
				list.add(Integer.parseInt(select.getmaxPrice()));
			}
			if(select.getGoodsQuantity()!=null&&!"".equals(select.getGoodsQuantity())&&!"0".equals(select.getGoodsQuantity())){			
				sql.append(" AND QUANTITY >=?");
				list.add(Integer.parseInt(select.getGoodsQuantity()));
			}
			if(select.getStatus()!=null&&!"".equals(select.getStatus()) && !"0".equals(select.getStatus())){
				sql.append(" AND STATUS =?");
				list.add(Integer.parseInt(select.getStatus()));
			}
			if(select.getGoodsName()!=null&&!"".equals(select.getGoodsName())){	
				sql.append(" AND (UPPER(GOODS_NAME) like ? or LOWER(GOODS_NAME) like ?)");
			list.add("%"+select.getGoodsName()+"%");
			list.add("%"+select.getGoodsName()+"%");
			}
			if(select.getSort()!=null && !"0".equals(select.getSort())&&!"".equals(select.getSort())){
				if(select.getSort().equals("1")){
					sql.append(" ORDER BY PRICE ");
				}else{
					sql.append(" ORDER BY PRICE DESC");
				}			
			}
			sql.append(")S)");
			
			if(made!=0 && kara!=0){
			sql.append("WHERE ROW_NUM >= ? AND ROW_NUM < ?");
			list.add(kara);
			list.add(made);
			}
			
			Object[]object=list.toArray();
			PreparedStatement stmt =conn.prepareStatement(sql.toString());
			
			for(int i =0;i<object.length;i++){
				stmt.setObject(i+1, object[i]);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Goods good = new Goods();
				good.setGoodsID(rs.getString("goods_ID"));
				good.setGoodsName(rs.getString("goods_Name"));
				good.setGoodsPrice(rs.getInt("Price"));
				good.setGoodsQuantity(rs.getInt("Quantity"));
				good.setGoodsImageName(rs.getString("Image_Name"));
				good.setStatus(rs.getString("status"));
				Goods.add(good);
			}
			
		} catch (SQLException e) {
		
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		return Goods;
	}


	public List<Goods> AllGoods() {
		List<Goods> Goods = new ArrayList<>();

		String querySQL = "SELECT * FROM BEVERAGE_GOODS";
		Connection conn = DBConnectionFactory.getOracleDBConnection();
		try(Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(querySQL)){
			while (rs.next()) {
				Goods good = new Goods();
				good.setGoodsID(rs.getString("goods_ID"));
				good.setGoodsName(rs.getString("goods_Name"));
				good.setGoodsPrice(rs.getInt("Price"));
				good.setGoodsQuantity(rs.getInt("Quantity"));
				good.setGoodsImageName(rs.getString("Image_Name"));
				good.setStatus(rs.getString("status"));
				Goods.add(good);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
			
				e.printStackTrace();
			}
		}
		return Goods;
	}
}
