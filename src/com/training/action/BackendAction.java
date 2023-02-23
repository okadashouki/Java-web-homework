package com.training.action;

import java.util.List;
import java.util.Set;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.training.formbean.BackendActionform;
import com.training.formbean.Selectfrom;
import com.training.model.Goods;
import com.training.model.SalesReport;
import com.training.model.Select;
import com.training.service.BackendService;
//測試網址 http://localhost:8085/JavaEE_Session4_Homework/BankLogin.html
@MultipartConfig
public class BackendAction extends DispatchAction{
	
	private BackendService backendService = BackendService.getInstance();
	
	
	public ActionForward Backend(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) {
		
		 return mapping.findForward("queryGoods");
	}
	
	 public ActionForward queryGoods(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) {
		 return mapping.findForward("Backend");
	 }
	 
	 
	 public ActionForward updateGoodsview(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 return mapping.findForward("updateGoodsview");
	 }
	 
	 
	 public ActionForward updateGoods(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 BackendActionform backendActionform = (BackendActionform) form;
		 Goods goods = new Goods();
			BeanUtils.copyProperties(goods, backendActionform);
		 boolean datas = backendService.updateGoods(goods);
			if (datas) {
				System.out.println("updateGoods...");
				System.out.println("商品維護作業成功！");
			} else {
				System.out.println("商品維護作業失敗！");
			}
		 return mapping.findForward("updateGoods");
	 }

	 
	 
	 public ActionForward addGoodsview(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 return mapping.findForward("addGoodsview");
	 }
	 
	 
	 
	 public ActionForward addGoods(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 BackendActionform backendActionform = (BackendActionform) form;
		 FormFile Imagename = backendActionform.getGoodsImage();
		 String fileName = Imagename.getFileName();
//		 response.sendRedirect("DrinksImage/" + fileName);
		 Goods goods = new Goods();
		 BeanUtils.copyProperties(goods, backendActionform);
		 goods.setGoodsImageName(fileName);
			
			int datas = backendService.createGoods(goods);
			if (datas > 0) {
				System.out.println("商品新增上架成功！ 商品編號：" + datas);
			}
		 
		 return mapping.findForward("addGoods");
	 }
	 
	 public ActionForward querySalesReportview(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 
		 return mapping.findForward("querySalesReportview");
	 }
	 
	 
	 public ActionForward querySalesReport(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 String startdate = request.getParameter("queryStartDate");
			String enddate = request.getParameter("queryEndDate");
		
			Set<SalesReport> reports = backendService.queryOrderBetweenDate(startdate, enddate);
			reports.stream().forEach(r -> System.out.println(r));
		 return mapping.findForward("querySalesReport");
	 }
	 
	 public ActionForward Selectgood(ActionMapping mapping, ActionForm form, 
	            HttpServletRequest request, HttpServletResponse response) throws Exception {
		 Selectfrom selectfrom = (Selectfrom) form;
		 Select select = new Select();
		 BeanUtils.copyProperties(select, selectfrom);
		 List<String> datas = backendService.getselect(select);
			System.out.println("---------------------------");
			datas.stream().forEach(g -> System.out.println(g));
		 
		 
		 return mapping.findForward("queryGoods");
	 }
	 
	 
	 
	 
}
