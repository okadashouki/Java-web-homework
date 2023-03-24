package com.training.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.training.action.DBConnectionFactory;
import com.training.model.Goods;
import com.training.model.frontendAction;

public class FrontendDao {
	
	private static FrontendDao frontendDao = new FrontendDao();
	private FrontendDao(){ }

	public static FrontendDao getInstance() {
	
		return frontendDao;
	}



	public Map<BigDecimal, Goods> queryBuyGoods(Set<BigDecimal> goodsIDs) {//ok
		Map<BigDecimal, Goods> goods = new LinkedHashMap<>();

		for (int i = 0; i < goodsIDs.size(); i++) {
			BigDecimal x = (BigDecimal) goodsIDs.toArray()[i];
			String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID =?";
			try (Connection conn = DBConnectionFactory.getOracleDBConnection();
					PreparedStatement stmt = conn.prepareStatement(querySQL)) {
				stmt.setBigDecimal(1, x);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Goods good = new Goods();
						good.setGoodsID(rs.getString("goods_ID"));
						good.setGoodsName(rs.getString("goods_Name"));
						good.setGoodsPrice(rs.getInt("Price"));
						good.setGoodsQuantity(rs.getInt("Quantity"));
						good.setGoodsImageName(rs.getString("Image_Name"));
						good.setStatus(rs.getString("status"));
						goods.put(x, good);
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
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return goods;	
	}

	public boolean batchCreateGoodsOrder(String customerID,
			Map<Goods, Integer> kazu) {
		java.util.Date utilDate = new java.util.Date();
		Set<Goods> keys = kazu.keySet();
		boolean insertSuccess = false;

		String insertSQL = "INSERT INTO BEVERAGE_ORDER(ORDER_ID,ORDER_DATE,CUSTOMER_ID,GOODS_ID,GOODS_BUY_PRICE,BUY_QUANTITY)";
		insertSQL += "VALUES(BEVERAGE_ORDER_SEQ.NEXTVAL,?,?,?,?,?)";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
			for (Iterator<Goods> i = keys.iterator(); i.hasNext();) {
				Goods key = i.next();
				Integer value = kazu.get(key);
				try {

					pstmt.setTimestamp(1, new Timestamp(utilDate.getTime()));
					pstmt.setString(2, customerID);
					pstmt.setString(3, key.getGoodsID());
					pstmt.setInt(4, key.getGoodsPrice());
					pstmt.setInt(5, value);
					pstmt.addBatch();

				} catch (SQLException e) {
					conn.rollback();
					throw e;
				}
			}
			int[] insertCount = pstmt.executeBatch();
			for (int s : insertCount) {
				if (s == 0) {
					insertSuccess = false;

					break;
				} else {
					insertSuccess = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insertSuccess;
	}

	public static boolean batchUpdateGoodsQuantity(Set<Goods> collect) {
		boolean updateSuccess = false;
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			conn.setAutoCommit(false);
			String updateSql = "UPDATE BEVERAGE_GOODS SET goods_Name =?,Price=?, ";
			updateSql += "Quantity = ?,Image_Name=?,status = ? WHERE goods_ID=?";
			try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
				for (Goods good : collect) {

					stmt.setString(1, good.getGoodsName());
					stmt.setInt(2, good.getGoodsPrice());
					stmt.setInt(3, good.getGoodsQuantity());
					stmt.setString(4, good.getGoodsImageName());
					stmt.setString(5, good.getStatus());
					stmt.setString(6, good.getGoodsID());
					stmt.addBatch();

				}
				int[] updateCount = stmt.executeBatch();
				for (int s : updateCount) {
					if (s == 0) {
						updateSuccess = false;
						break;
					} else {
						updateSuccess = true;
					}
				}

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
		return updateSuccess;
	}

	public List<Goods> selectGoods(frontendAction deta, int made, int kara) {
		List<Goods> Goods = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		ArrayList<Object> list= new ArrayList<Object>();
		sql.append("SELECT * FROM(SELECT ROWNUM ROW_NUM, S.* FROM ");
		sql.append(" (SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID IS NOT NULL");
		
		Connection conn = DBConnectionFactory.getOracleDBConnection();
		try {
			
			if(deta.getSearchKeyword()!=null&&!"".equals(deta.getSearchKeyword())){	
				sql.append(" AND (UPPER(GOODS_NAME) like ? or LOWER(GOODS_NAME) like ?)");
			list.add("%"+deta.getSearchKeyword()+"%");
			list.add("%"+deta.getSearchKeyword()+"%");
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
				good.setDESCRIPTIO(rs.getString("DESCRIPTION"));
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

	public Goods good(String goodsID) {
		String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID =?";
		String x =  goodsID;
		Goods good = new Goods();
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement stmt = conn.prepareStatement(querySQL)) {
			stmt.setString(1, x);
		
			try (ResultSet rs = stmt.executeQuery()) {
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
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	
	return good;	
}


}
