package com.training.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.training.dao.FrontendDao;
import com.training.model.Goods;
import com.training.model.frontendAction;



public class FrontendServic {
	
	private static FrontendServic frontendServic = new FrontendServic();

	private FrontendServic(){ }	
	
	public static FrontendServic getInstance(){
		return frontendServic;
	}
	
	private static FrontendDao FrontDao = FrontendDao.getInstance();

	public List<Goods> getDataBaseData(int pageNo) {
		int x = pageNo;

		int made = x * 6 + 1;
		int kara = made - 6;
		List<Goods> oracleDatas = FrontDao.searchGoods(made, kara);
		return oracleDatas;
	}

	public Set<Goods> getDataBaseData(String searchKeyword, int pageNo) {
		int x = pageNo;
		int made = x * 6 + 1;
		int kara = made - 6;
		Set<Goods> oracleDatas = FrontDao.searchGoods(searchKeyword, made,
				kara);
		return oracleDatas;
	}

	public int buyGoods(List<String> values, List<String> buyQuantity,//ok
			int inputMoney) {
		int total = 0;
		for (int x = 0; x < values.size(); x++) {
			if (Integer.parseInt(buyQuantity.get(x)) != 0) {//如果購買數量不為0,取得商品地圖
				Set<BigDecimal> goodsIDs = new HashSet<>();
				goodsIDs.add(new BigDecimal(values.get(x)));
				Map<BigDecimal, Goods> buyGoods = FrontDao
						.queryBuyGoods(goodsIDs);
				Set<BigDecimal> keys = buyGoods.keySet();
				for (Iterator<BigDecimal> i = keys.iterator(); i.hasNext();) {
					BigDecimal key = i.next();
					Goods value = buyGoods.get(key);
					total += Integer.parseInt(buyQuantity.get(x))
							* value.getGoodsPrice();
				}
			}
		}
		
		return total;
	}

	public Map<Goods, Integer> kazu(List<String> values, List<String> buyQuantity,
			int inputMoney) {
		Map<Goods,Integer> goodsOrders = new HashMap<>();
		for (int x = 0; x < values.size(); x++) {
			if (Integer.parseInt(buyQuantity.get(x)) != 0) {
				Set<BigDecimal> goodsIDs = new HashSet<>();
				goodsIDs.add(new BigDecimal(values.get(x)));
				Map<BigDecimal, Goods> buyGoods = FrontDao.queryBuyGoods(goodsIDs);
				Set<BigDecimal> keys = buyGoods.keySet();
				for (Iterator<BigDecimal> i = keys.iterator(); i.hasNext();) {
					BigDecimal key = i.next();
					Goods value = buyGoods.get(key);
					Integer GoodsQuantity=value.getGoodsQuantity();
					if(GoodsQuantity>Integer.parseInt(buyQuantity.get(x))){ //庫存>購買
					goodsOrders.put(value,Integer.parseInt(buyQuantity.get(x)));//還要扣庫存
					
					}else if(GoodsQuantity<Integer.parseInt(buyQuantity.get(x))&&value.getGoodsQuantity()!=0){ //庫存小於購買,且庫存不等於0
						goodsOrders.put(value,value.getGoodsQuantity());//庫存扣至0
						
					}else{
						goodsOrders.put(value,0);
						
					}
				}
			}
		}
		return goodsOrders;
	}

	public int goodstotal2(Map<Goods, Integer> kazu) {
		Set<Goods> keys = kazu.keySet();
		int total = 0;
		for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
		   Goods key = i.next();
		   // 再取key的同時也將value一併取回
		   Integer value = kazu.get(key);
		   total+=key.getGoodsPrice()*value;
		}		
		return total;
	}

	public Map<Integer, Goods> CreateGoods(Map<Goods, Integer> kazu,
			String customerID) {
		boolean insertSuccess = FrontDao.batchCreateGoodsOrder(customerID,kazu); //新增訂單資料
		Map<Integer,Goods> goodsOrders = new HashMap<>();
		Set<Goods> keys = kazu.keySet();
		for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
		   Goods key = i.next();
		   // 再取key的同時也將value一併取回
		   Integer value = kazu.get(key);
		   goodsOrders.put(value, key);
		}		
		return goodsOrders;
	}

	public static boolean UpdateGoodsQuantity(Map<Goods, Integer> kazu) {
		boolean updateSuccess = FrontDao.batchUpdateGoodsQuantity(kazu.keySet().stream().collect(Collectors.toSet()));		
		return updateSuccess;
	}

	public List<Goods> getAllgood(frontendAction deta) {
		List<Goods> oracleDatas = FrontDao.selectGoods(deta,0,0);
		return oracleDatas;
	}

	public List<Goods> getgood(frontendAction deta) {
		int x = deta.getPageNo();
		if(x==0){
			x=1;
		}
		int made = x * 6 + 1;
		int kara = made - 6;
		List<Goods> oracleDatas =FrontDao.selectGoods(deta,made,kara);
		return oracleDatas;
	}
	

}
