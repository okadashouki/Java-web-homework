package com.training.action;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.training.model.Goods;

@MultipartConfig
public class MemberAction extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String action = req.getParameter("action");
		switch (action) {
			case "addCartGoods":
				// 商品加入購物車
				addCartGoods(req, resp);
				break;
			case "queryCartGoods":
				// 查詢購物車商品
				queryCartGoods(req, resp);
				break;
			case "clearCartGoods":
				// 清空購物車
				clearCartGoods(req, resp);
				break;
		}		
		// 重導回原頁面
		resp.sendRedirect("VendingMachine.html");
	}

	private void addCartGoods(HttpServletRequest req, HttpServletResponse resp) {
		// 商品加入購物車
		String goodsID = req.getParameter("goodsID");
		String buyQuantity = req.getParameter("buyQuantity");		
		System.out.println("goodsID:" + goodsID);
		System.out.println("buyQuantity:" + buyQuantity);	
		
		// 查詢資料庫商品並且加入購物車
//		Goods goods = frontEndDao.queryByGoods(goodsID); 
		Goods goods = null;
		
		Map<Goods, Integer> carGoods = new LinkedHashMap<>();
		carGoods.put(goods, Integer.parseInt(buyQuantity));
		
		HttpSession session = req.getSession();
		session.setAttribute("carGoods" , carGoods);		
	}

	private void queryCartGoods(HttpServletRequest req, HttpServletResponse resp) {
		// 列出商品名稱、購買數量
		// 購物車商品總金額
		
	}
	
	private void clearCartGoods(HttpServletRequest req, HttpServletResponse resp) {
		// 清空購物車
		
	}
	
}
