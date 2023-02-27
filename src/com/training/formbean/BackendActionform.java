package com.training.formbean;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class BackendActionform extends ActionForm{
	
	private String goodsID;
	private String goodsName;
	private int goodsPrice;
	private int goodsQuantity;
	private FormFile goodsImage;
	private String status;
	
	public String getGoodsID() {
		return goodsID;
	}
	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(int goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public int getGoodsQuantity() {
		return goodsQuantity;
	}
	public void setGoodsQuantity(int goodsQuantity) {
		this.goodsQuantity = goodsQuantity;
	}
	public FormFile getGoodsImage() {
		return goodsImage;
	}
	public void setGoodsImage(FormFile goodsImage) {
		this.goodsImage = goodsImage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
