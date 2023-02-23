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
	//尋找條件內全部商品
	public List<Goods> getselect(Select select) {
//		List<Goods> datas = new ArrayList<>();
		List<Goods> oracleDatas = bankDao.selectGoods(select,0,0);
//		for (Goods oracleData : oracleDatas) {
//			datas.add(oracleData.toString());
//		}
		return oracleDatas;
	}
	//尋找條件內全部商品 10筆指定資料(透過頁數指定)
	public List<Goods> getselect(Select select, int pageNo) {
		int x = pageNo;
		if(x==0){
			x=1;
		}
		int made = x * 10 + 1;
		int kara = made - 10;
		
//		List<Goods> datas = new ArrayList<>();
		List<Goods> oracleDatas = bankDao.selectGoods(select,made,kara);
		
		return oracleDatas;
	}


}
