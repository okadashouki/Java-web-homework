package com.training.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	
	
	
	 public ActionForward searchGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 FrontendActionform frontendActionform = (FrontendActionform) form;
		 frontendAction deta = new frontendAction();
		 BeanUtils.copyProperties(deta, frontendActionform);
		 String searchKeyword = deta.getSearchKeyword();
			int pageNo = deta.getPageNo();
		
		 if (searchKeyword == null) {
				List<Goods> datas = frontendServic.getDataBaseData(pageNo);
				System.out.println("第" + pageNo + "頁");
				datas.stream().forEach(g -> System.out.println(g));
			} else {
				Set<Goods> datas = frontendServic.getDataBaseData(searchKeyword,
						pageNo);
				System.out.println("關鍵字搜尋:" + searchKeyword);
				datas.stream().forEach(g -> System.out.println(g));
			}
		 
		 
		 return mapping.findForward("searchgood");
	 }
	
	 
	 public ActionForward VendingMachineview(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 
		 return mapping.findForward("VendingMachineview");
	 }
	 
	 
	 
	 public ActionForward buyGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 FrontendActionform frontendActionform = (FrontendActionform) form;
		 buygoodaction buygood = new buygoodaction();
		 BeanUtils.copyProperties(buygood, frontendActionform);
		 HttpServletRequest httpRequest = (HttpServletRequest)request;		
			HttpSession session = httpRequest.getSession();
//		 HttpSession session = request.getSession();
			Account account = (Account) session.getAttribute("account");

		 List<String> buyQuantity =  new ArrayList();
		 List<String>Values= new ArrayList();
	
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 if(carGoods==null){
			 System.out.println("購物車是空的，請選擇商品");
			 return mapping.findForward("searchgood");
		 }
		 
		 Set<Goods> keys = carGoods.keySet();
			for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
				Goods key = i.next();	
				Integer value = carGoods.get(key);
				Values.add((key.getGoodsID()));
				buyQuantity.add(value.toString());
			}	 
		 boolean datas = frontendServic.buyGoods(Values, buyQuantity, //判斷是否購買成功
				 buygood.getInputMoney());
		 if (datas == false) {
				int goodstotal = frontendServic.goodstotal(Values, buyQuantity,
						buygood.getInputMoney());
				System.out.println("投入金額:" + buygood.getInputMoney());
				System.out.println("購買金額:" + goodstotal);
				System.out.println("找零金額:" + buygood.getInputMoney());
				System.out.println("--------購買失敗--------");
			} else {
				int x = buygood.getInputMoney();
				//key商品 vaule購買數量
				Map<Goods, Integer> kazu = frontendServic.kazu(Values, buyQuantity, //判斷商品庫存並塞入Map
						buygood.getInputMoney());

				int goodstotal = frontendServic.goodstotal2(kazu);
				Set<Goods> keys2 = kazu.keySet();
				System.out.println("投入金額:" + x);
				System.out.println("購買金額:" + goodstotal);
				System.out.println("找零金額:" + (x - goodstotal));
				for (Iterator<Goods> i = keys2.iterator(); i.hasNext();) {
					Goods key = i.next();
					Integer value = kazu.get(key);
					if (value != 0) {
						System.out.println("商品名稱:" + key.getGoodsName() + " "
								+ "商品金額:" + key.getGoodsPrice() + " " + "購買數量:"
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
				return mapping.findForward("clearCartGoods");
			}
		 return mapping.findForward("searchgood");
	 }
//		Map<Goods, Integer> carGoods = new LinkedHashMap<>();
		
		
	 public ActionForward addCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 	String goodsID = request.getParameter("goodsID");
		 	String goodsiD = goodsID;
			String buyQuantity = request.getParameter("buyQuantity");		
			System.out.println("goodsID:" + goodsID);
			System.out.println("buyQuantity:" + buyQuantity);
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
		 return mapping.findForward("searchgood");
	 }
	 
	 
	 
	 public ActionForward queryCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 HttpSession session = request.getSession();
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 int total = 0;
		 if(carGoods==null){
			 System.out.println("目前購物車沒有商品，快去購物吧~");
		 }else{
			 Set<Goods> keys = carGoods.keySet();
			 System.out.println("目前購物車商品:");
				for(Iterator<Goods> i = keys.iterator(); i.hasNext();){
					Goods key = i.next();	
					Integer value = carGoods.get(key);
			 System.out.println("商品名稱:"+key.getGoodsName());
			 System.out.println("商品數量:"+value);
			 int price = key.getGoodsPrice()*value;
			 total+=price;
				}
		 }
			 System.out.println("總金額:"+total);		
				
		 return mapping.findForward("searchgood");
	 }
	 
	 
	 public ActionForward clearCartGoods(ActionMapping mapping, ActionForm form, 
	          HttpServletRequest request, HttpServletResponse response)  throws Exception{
		 HttpSession session = request.getSession();
		 Map<Goods, Integer>  carGoods= (Map<Goods, Integer>) session.getAttribute("carGoods");
		 
		 session.removeAttribute("carGoods");
		 System.out.println("已清除購物車");
		 return mapping.findForward("VendingMachine");
	 }
	 
}
