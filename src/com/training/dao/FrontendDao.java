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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.training.action.DBConnectionFactory;
import com.training.model.Goods;

public class FrontendDao {
	
	private static FrontendDao frontendDao = new FrontendDao();
	private FrontendDao(){ }

	public static FrontendDao getInstance() {
	
		return frontendDao;
	}

	public static List<Goods> searchGoods(int made, int kara) {
		List<Goods> Goodsdatas = new ArrayList<>();
		String querySQL = "SELECT * FROM (SELECT ROWNUM ROW_NUM, S.* FROM BEVERAGE_GOODS S )";
		querySQL += "WHERE ROW_NUM >= ? AND ROW_NUM < ?";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement stmt = conn.prepareStatement(querySQL)) {
			stmt.setInt(1, kara);
			stmt.setInt(2, made);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Goods good = new Goods();
				good.setGoodsID(rs.getBigDecimal("goods_ID"));
				good.setGoodsName(rs.getString("goods_Name"));
				good.setGoodsPrice(rs.getInt("Price"));
				good.setGoodsQuantity(rs.getInt("Quantity"));
				good.setGoodsImageName(rs.getString("Image_Name"));
				good.setStatus(rs.getString("status"));
				Goodsdatas.add(good);}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return Goodsdatas;
	}
	
	
	public static Set<Goods> searchGoods(String searchKeyword, int made,
			int kara) {
		Set<Goods> goods = new LinkedHashSet<>();
		String querySQL = "SELECT * FROM (SELECT ROWNUM ROW_NUM, S.* FROM BEVERAGE_GOODS S ";
		querySQL += " WHERE UPPER(GOODS_NAME) like ? or LOWER(GOODS_NAME) like ?) ";
		querySQL += " WHERE ROW_NUM >= ? AND ROW_NUM < ?";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()){
				PreparedStatement stmt = conn.prepareStatement(querySQL);
			stmt.setString(1, "%" + searchKeyword + "%");
			stmt.setString(2, "%" + searchKeyword + "%");
			stmt.setInt(3, kara);
			stmt.setInt(4, made);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Goods good = new Goods();
				good.setGoodsID(rs.getBigDecimal("goods_ID"));
				good.setGoodsName(rs.getString("goods_Name"));
				good.setGoodsPrice(rs.getInt("Price"));
				good.setGoodsQuantity(rs.getInt("Quantity"));
				good.setGoodsImageName(rs.getString("Image_Name"));
				good.setStatus(rs.getString("status"));
				goods.add(good);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return goods;
	}

	public Map<BigDecimal, Goods> queryBuyGoods(Set<BigDecimal> goodsIDs) {
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
						good.setGoodsID(rs.getBigDecimal("goods_ID"));
						good.setGoodsName(rs.getString("goods_Name"));
						good.setGoodsPrice(rs.getInt("Price"));
						good.setGoodsQuantity(rs.getInt("Quantity"));
						good.setGoodsImageName(rs.getString("Image_Name"));
						good.setStatus(rs.getString("status"));
						goods.put(x, good);
					}
				} catch (SQLException e) {
					e.printStackTrace();
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
					pstmt.setBigDecimal(3, key.getGoodsID());
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
					stmt.setBigDecimal(6, good.getGoodsID());
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
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return updateSuccess;
	}

}
