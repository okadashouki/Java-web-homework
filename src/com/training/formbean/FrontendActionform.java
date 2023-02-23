package com.training.formbean;

import org.apache.struts.action.ActionForm;

public class FrontendActionform extends ActionForm{
	
	private String searchKeyword;//關鍵字
	private int  pageNo;//頁數
	private int inputMoney;//投入金額
	private String buyQuantity;//購買數量
	private String goodsID;//商品編號
	private String customerID;//顧客ID
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getInputMoney() {
		return inputMoney;
	}
	public void setInputMoney(int inputMoney) {
		this.inputMoney = inputMoney;
	}
	public String getBuyQuantity() {
		return buyQuantity;
	}
	public void setBuyQuantity(String buyQuantity) {
		this.buyQuantity = buyQuantity;
	}
	public String getGoodsID() {
		return goodsID;
	}
	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	

}
