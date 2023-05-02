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


	public Map<Integer, Goods> CreateGoods(Map<Goods, Integer> carGoods,
			String customerID) {
		boolean insertSuccess = FrontDao.batchCreateGoodsOrder(customerID,carGoods); //新增訂單資料
		Map<Integer,Goods> goodsOrders = new HashMap<>();
		Set<Goods> keys = carGoods.keySet();
		for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
		   Goods key = i.next();
		   // 再取key的同時也將value一併取回
		   Integer value = carGoods.get(key);
		   goodsOrders.put(value, key);
		}		
		return goodsOrders;
	}

	public static boolean UpdateGoodsQuantity(Map<Goods, Integer> carGoods) {
		boolean updateSuccess = FrontDao.batchUpdateGoodsQuantity(carGoods.keySet().stream().collect(Collectors.toSet()));		
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

	public Goods good(String goodsID) {
		
		return FrontDao.good(goodsID);
	}
	

}
