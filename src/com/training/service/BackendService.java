package com.training.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.training.dao.BackendDao;
import com.training.model.Goods;
import com.training.model.SalesReport;
import com.training.model.Select;


public class BackendService {
	
	private static BackendService backendService = new BackendService();

	private BackendService(){ }	
	
	public static BackendService getInstance(){
		return backendService;
	}
	
	private BackendDao bankDao = BackendDao.getInstance();

	public boolean updateGoods(Goods goods) {
		List<Goods> oracleDatas = bankDao.shohinsagasu(goods);
		Goods goodsInDB = oracleDatas.get(0);
		goodsInDB.setGoodsPrice(goods.getGoodsPrice());
		goodsInDB.setStatus(goods.getStatus());

		boolean updateSuccess = bankDao.updateGoods(goodsInDB);

		return updateSuccess;
	}

	public int createGoods(Goods goods) {
		
		int goodsID = bankDao.createGoods(goods);
		return goodsID;
		
	}

	public Set<SalesReport> queryOrderBetweenDate(String startdate,
			String enddate) {
		while (startdate.indexOf("-")!=-1){
			startdate=startdate.replace("-", "/");
		}
		while (enddate.contains("-")){
			enddate=enddate.replace("-", "/");
		}
	
		Set<SalesReport> queryOrderBetweenDate=bankDao.queryOrderBetweenDate(startdate,
				enddate);
		
		return queryOrderBetweenDate;
	}

	public List<String> getselect(Select select) {
		List<String> datas = new ArrayList<>();
		List<Goods> oracleDatas = bankDao.selectGoods(select);
		for (Goods oracleData : oracleDatas) {
			datas.add(oracleData.toString());
		}
		return datas;
	}

}
