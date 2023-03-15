package com.training.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.training.dao.BackendDao;
import com.training.formbean.FrontendActionform;
import com.training.model.Account;
import com.training.model.Goods;
import com.training.model.buygoodaction;
import com.training.model.frontendAction;
import com.training.service.FrontendServic;
//測試網址 http://localhost:8085/JavaEE_Session4_Homework/Login.jsp


public class FrontendAction extends DispatchAction{

	private FrontendServic frontendServic = FrontendServic.getInstance();
	

	 public ActionForward VendingMachine(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 form=null;
		 return mapping.findForward("VendingMachine");
	 }
	 public ActionForward CartGoodsview(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 HttpSession session = request.getSession();
		 Map<Goods, Integer> carGoods = (Map<Goods, Integer>) session.getAttribute("carGoods");
		 if (carGoods != null) {
		     Set<Goods> keys = carGoods.keySet();
		     for (Iterator<Goods> i = keys.iterator(); i.hasNext();) {
		         Goods key = i.next();
		         Goods updatedGoods = frontendServic.good(key.getGoodsID()); // 取得要更改的商品
		         if (updatedGoods != null) {
		           
		             key.setGoodsQuantity(updatedGoods.getGoodsQuantity());
		         }
		     }
		 }

		 
		 return mapping.findForward("CartGoodsview");
	 }
	 
	 //刷新商品頁面
	 public ActionForward VendingMachineview(ActionMapping mapping, ActionForm form, //ok
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 FrontendActionform frontendActionform = (FrontendActionform) form;
		 frontendAction deta = new frontendAction();
		 String urlMsg = null;
		 BeanUtils.copyProperties(deta, frontendActionform);;
		 List<Goods> datas = frontendServic.getAllgood(deta);//取得全部商品資訊
		 List<Goods> goods=frontendServic.getgood(deta);
		 Set<Integer>pagetotals = new TreeSet<>();
		 request.setAttribute("goods", goods); 
		 Double pagelogic = Math.ceil((datas.size()/6.0));
		 int pagetotal = pagelogic.intValue();
		 Set<Integer>pages = new TreeSet<>();
		 for(int x=1;x<=pagetotal;x++){//[1,2,3]
			 pagetotals.add(x);
		 }
		 //為了只顯示3筆頁數
		 for(Integer p: pagetotals){
			 if(deta.getPageNo()==1){
				 for(int x=1;x<=3;x++)
					 if(x<=pagetotals.size()){
				 pages.add(x);
					 }
			 }else if(pagetotal == deta.getPageNo()){
				 for(int x=pagetotal-2;x<=pagetotal;x++){
					 if(x>=1){
					 pages.add(x);
					 }
				 }
		 }else if(p == deta.getPageNo()){
			 for(int x=p;x<p+3;x++){
				 if(p<pagetotal){
					 pages.add(x-1); 
				 	}
			 	}
		 	}
		 }
		
		 request.setAttribute("deta", deta);//把搜尋條件補回前端頁面
		 request.setAttribute("pagetotals", pagetotals);//總頁數
		 request.setAttribute("pages", pages);//只顯示3筆頁數
		 return mapping.findForward("VendingMachineview");
	 }
	 
	 
	 
	 public ActionForward buyGoods(ActionMapping mapping, ActionForm form, //ok
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 FrontendActionform frontendActionform = (FrontendActionform) form;
		 buygoodaction buygood = new buygoodaction();
		 BeanUtils.copyProperties(buygood, frontendActionform);
		 HttpServletRequest httpRequest = (HttpServletRequest)request;		
			HttpSession session = httpRequest.getSession();
			Account account = (Account) session.getAttribute("account");

		 List<String> buyQuantity =  new ArrayList();
		 List<String>Values= new ArrayList();
		ArrayList buyGoodsMsg = new ArrayList();
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 if(carGoods==null){
			 buyGoodsMsg.add("購物車是空的，請選擇商品");
			 session.setAttribute("buyGoodsMsg", buyGoodsMsg);
			 return mapping.findForward("VendingMachine");
		 }
		 
		 Set<Goods> keys = carGoods.keySet();
			for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
				Goods key = i.next();	
				Integer value = carGoods.get(key);
				Values.add((key.getGoodsID()));
				buyQuantity.add(value.toString());
			}	 
			int goodstotal = frontendServic.buyGoods(Values, buyQuantity, //算出總金額
				 buygood.getInputMoney());
		 boolean buyGood = false;
		 if (buygood.getInputMoney() < goodstotal) {//判斷是否購買成功
				buyGood = false;
			
			} else {
				buyGood = true;		
			}
		 if (buyGood == false) {

				buyGoodsMsg.add("投入金額:" + buygood.getInputMoney());
				buyGoodsMsg.add("購買金額:" + goodstotal+"\r\n");
				buyGoodsMsg.add("找零金額:" + buygood.getInputMoney());
				buyGoodsMsg.add("----購買失敗----");
				session.setAttribute("buyGoodsMsg", buyGoodsMsg);
				return mapping.findForward("CartGoodsview");
			} else {
				int x = buygood.getInputMoney();
				//key商品 vaule購買數量
				Map<Goods, Integer> kazu = frontendServic.kazu(Values, buyQuantity, //判斷商品庫存並塞入Map
						buygood.getInputMoney());

				int railgoodstotal = frontendServic.goodstotal2(kazu);
				Set<Goods> keys2 = kazu.keySet();
				buyGoodsMsg.add("投入金額:" + x);
				buyGoodsMsg.add("購買金額:" + railgoodstotal);
				buyGoodsMsg.add("找零金額:" + (x - railgoodstotal));
				for (Iterator<Goods> i = keys2.iterator(); i.hasNext();) {
					Goods key = i.next();
					Integer value = kazu.get(key);
					if (value != 0) {
						buyGoodsMsg.add("商品名稱:" + key.getGoodsName() +" "
								+ "商品金額:" + key.getGoodsPrice() +" " + "購買數量:"
								+ value);
					}
				}
				Map<Integer, Goods> CreateGoods = frontendServic.CreateGoods(kazu,
						account.getId());
//				
				Set<Goods> keys3 = kazu.keySet();
				for (Iterator<Goods> i = keys3.iterator(); i.hasNext();) { //扣庫存數量
					Goods key = i.next();
					// 再取key的同時也將value一併取回
					Integer value = kazu.get(key);
					key.setGoodsQuantity(key.getGoodsQuantity()-value);
				}
				boolean updateSuccess = frontendServic //更新資料庫
						.UpdateGoodsQuantity(kazu);
				 session.setAttribute("buyGoodsMsg", buyGoodsMsg);
				 session.removeAttribute("carGoods");
				return mapping.findForward("VendingMachine");
			}
//		 session.setAttribute("buyGoodsMsg", buyGoodsMsg);
//		 return mapping.findForward("VendingMachine");
	 }
		
	 public ActionForward addCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 	String goodsID = request.getParameter("goodsID");
		 	String goodsiD = goodsID;
			String buyQuantity = request.getParameter("buyQuantity");	
			JSONArray cartMsg = new JSONArray();
			if(!"".equals(buyQuantity)&&Integer.parseInt(buyQuantity)>0){
				cartMsg.add("goodsID:" + goodsID);
				cartMsg.add("buyQuantity:" + buyQuantity);
			Goods goods =  BackendDao.shohinsagasu(goodsiD);
			HttpSession session = request.getSession();
			
			//先取得購物車
			Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
			boolean flag = true;

			if(carGoods==null){   //如果取不到就新增購物車
				carGoods = new LinkedHashMap<Goods, Integer>();
//				
			}else{   //如果有取到購物車就找購物車裡是否有同樣商品
				Set<Goods> keys = carGoods.keySet();
				for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
					Goods key = i.next();	//要改變的商品
					Integer value = carGoods.get(key);//要改變的商品原本數量
				if(	goodsiD.equals(key.getGoodsID())){
					flag = false; //如果有同樣商品，重新更新那個商品的購買數量
					carGoods.put(key,value+ Integer.parseInt(buyQuantity));
					}
				}
			}
			if(flag == true){
				carGoods.put(goods, Integer.parseInt(buyQuantity));
			}
			session.setAttribute("carGoods" , carGoods); //更新購物車
			}else{
				cartMsg.add("請輸入數量");
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(cartMsg);
			out.flush();
			out.close();
		 return null;
	 }
	 
	 
	 
	 public ActionForward queryCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 HttpSession session = request.getSession();
		 JSONArray cartMsg = new JSONArray();
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 int total = 0;
		 if(carGoods==null){
			 cartMsg.add("目前購物車沒有商品，快去購物吧~");
		 }else{
			 Set<Goods> keys = carGoods.keySet();
			 cartMsg.add("目前購物車商品:");
				for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
					Goods key = i.next();	
					Integer value = carGoods.get(key);
					cartMsg.add("商品名稱:"+key.getGoodsName());
					cartMsg.add("商品數量:"+value);
			 int price = key.getGoodsPrice()*value;
			 total+=price;
				}
		 }
		 cartMsg.add("總金額:"+total);		
		 response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(cartMsg);
			out.flush();
			out.close();
		 return null;
	 }
	 
	 
	 public ActionForward clearCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 HttpSession session = request.getSession();
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 JSONArray cartMsg = new JSONArray();
		 session.removeAttribute("carGoods");
		 cartMsg.add("已清除購物車");
		 response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(cartMsg);
			out.flush();
			out.close();
		 return null;
	 }
	 public ActionForward removeItem(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 
		 // 從請求中取得要刪除的商品 ID
		    String goodsId = (request.getParameter("goodsID"));
		    
		    // 從 session 中取得購物車
		    Map<Goods, Integer> carGoods = (Map<Goods, Integer>) request.getSession().getAttribute("carGoods");
		    
		    // 從購物車中移除該商品
		    Iterator<Map.Entry<Goods, Integer>> iterator = carGoods.entrySet().iterator();
		    while (iterator.hasNext()) {
		        Map.Entry<Goods, Integer> entry = iterator.next();
		        if (entry.getKey().getGoodsID().equals(goodsId)) {
		            iterator.remove();
		            break;
		        }
		    }
		    
		    // 將更新後的購物車放回 session
		    request.getSession().setAttribute("carGoods", carGoods);
		    
		    // 重新導向到顯示購物車的頁面
		 
		 return mapping.findForward("CartGoodsview");
	 }
	 
	 /**
	  * 更新購物車中某一商品的數量
	  * 從request中取得商品ID及新數量
	  * 從session中取得購物車清單(Map<Goods, Integer>)及目前總金額(int)
	  * 在購物車清單中找到對應的商品，更新數量
	  * 重新計算購物車中每件商品的小計及總金額
	  * 更新session中的購物車清單及目前總金額
	  * 跳轉到cart.jsp
	  *
	  
	  */
	 public ActionForward updateItem(ActionMapping mapping, ActionForm form,
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 FrontendActionform frontendActionform = (FrontendActionform) form;
		 buygoodaction buygood = new buygoodaction();
		 BeanUtils.copyProperties(buygood, frontendActionform);
	     // 從request中取得商品ID及新數量
		 String goodsId = request.getParameter("goodsId");
	     int quantity = Integer.parseInt(request.getParameter("newQuantity"));
	     // 從session中取得購物車清單(Map<Goods, Integer>)及目前總金額(int)
	     HttpSession session = request.getSession();
	     Map<Goods, Integer> carGoods = (Map<Goods, Integer>) session.getAttribute("carGoods");
	     // 在購物車清單中找到對應的商品，更新數量
	     for (Goods goods : carGoods.keySet()) {
	         if (goods.getGoodsID().equals(goodsId)) {
	             carGoods.put(goods, quantity);
	             break;
	         }
	     }
	     // 重新計算購物車中每件商品的小計及總金額
	     int total = 0;
	     for (Map.Entry<Goods, Integer> entry : carGoods.entrySet()) {
	         Goods goods = entry.getKey();
	         int quantityInCart = entry.getValue();
	         int subTotal = goods.getGoodsPrice() * quantityInCart;
	         total += subTotal;
	     }
	     // 更新session中的購物車清單及目前總金額
	     session.setAttribute("carGoods", carGoods);
	     session.setAttribute("total", total);
	     List<Map<String, Object>> carGoodsList = new ArrayList<>();
	     
	     for (Map.Entry<Goods, Integer> entry : carGoods.entrySet()) {
	         Goods goods = entry.getKey();
	         int quantityInCart = entry.getValue();
	         Map<String, Object> map = new HashMap<>();
	         map.put("goodsID", goods.getGoodsID());
	         map.put("goodsName", goods.getGoodsName());
	         map.put("goodsPrice", goods.getGoodsPrice());
	         map.put("quantity", quantityInCart);
	         map.put("subTotal", String.valueOf(goods.getGoodsPrice() * quantityInCart));
	         carGoodsList.add(map);
	     }
//	     JSONArray carGoodsJsonArray = JSONArray.fromObject(carGoodsList);
	  
	     response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println(JSONArray.fromObject(carGoodsList));

			out.flush();
			out.close();
			 form=null;
			return null;
	 }
}
