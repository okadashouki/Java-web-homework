package com.training.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

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

//測試網址 http://localhost:8085/JavaEE_Session4_Homework/Login.jsp
@MultipartConfig
public class BackendAction extends DispatchAction {

	private BackendService backendService = BackendService.getInstance();

	public ActionForward Backend(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		return mapping.findForward("queryGoods");
	}

	public ActionForward queryGoods(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		return mapping.findForward("Backend");
	}

	public ActionForward addGoodsview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward("addGoodsview");
	}

	public ActionForward updateGoodsview(ActionMapping mapping,//ok
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 商品選單資料
		List<Goods> datas = backendService.getallgoods();
		request.setAttribute("datas", datas);
		// 被選擇要修改的商品資料
		String id = (String) request.getSession().getAttribute("modifyGoodID");
		if (id != null) {
			Goods goods = backendService.getIDgoods(id);
			request.setAttribute("updateGoods", goods);
		}
		return mapping.findForward("updateGoodsview");
	}

	// for AJAX 使用
	public ActionForward getModifygood(ActionMapping mapping, ActionForm form,//ok
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// 被選擇要修改的商品資料
		String goodsID = request.getParameter("id");
		Goods goods = backendService.getIDgoods(goodsID);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(JSONObject.fromObject(goods));
		out.flush();
		out.close();

		return null;
	}

	public ActionForward updateGoods(ActionMapping mapping, ActionForm form,//ok
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession();
		String updateGoodsMsg = null;
		BackendActionform backendActionform = (BackendActionform) form;
		Goods goods = new Goods();
		BeanUtils.copyProperties(goods, backendActionform);
		boolean datas = backendService.updateGoods(goods);
		if (datas) {

			updateGoodsMsg = "商品維護作業成功！";
		} else {
			updateGoodsMsg = "商品維護作業失敗！";
		}
		session.setAttribute("modifyGoodID", goods.getGoodsID());
		session.setAttribute("updateGoodsMsg", updateGoodsMsg);
		return mapping.findForward("updateGoods");
	}

	public ActionForward addGoods(ActionMapping mapping, ActionForm form,//ok
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BackendActionform backendActionform = (BackendActionform) form;
		FormFile Imagename = backendActionform.getGoodsImage();
		String fileName = Imagename.getFileName();
		HttpSession session = request.getSession();
//		 response.sendRedirect("DrinksImage/" + fileName);
		Goods goods = new Goods();
		BeanUtils.copyProperties(goods, backendActionform);
		goods.setGoodsImageName(fileName);
		String addGoodsMsg = null;
		int datas = backendService.createGoods(goods);
		if (datas > 0) {
			addGoodsMsg = "商品新增上架成功！ 商品編號：" + datas;
		}
		session.setAttribute("addGoodsMsg", addGoodsMsg);
		return mapping.findForward("addGoods");
	}

	public ActionForward querySalesReportview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("querySalesReportview");
	}

	public ActionForward querySalesReport(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String startdate = request.getParameter("queryStartDate");
		String enddate = request.getParameter("queryEndDate");
		HttpSession session = request.getSession();

		Set<SalesReport> reports = backendService.queryOrderBetweenDate(
				startdate, enddate);
		session.setAttribute("reports", reports);
		return mapping.findForward("querySalesReport");
	}

	public ActionForward Selectgood(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Selectfrom selectfrom = (Selectfrom) form;
		Select select = new Select();
		BeanUtils.copyProperties(select, selectfrom); // 取得搜尋條件
		int pageNo = select.getPageNo();
		List<Goods> datas = backendService.getselectall(select);// 尋找搜尋條件所有商品
		List<Goods> data = backendService.getselect(select);// 尋找搜尋條件包含頁數商品
		Double pagelogic = Math.ceil((datas.size() / 10.0));// (一頁有10件商品)
		Set<Integer> pagetotals = new TreeSet<>();
		request.setAttribute("data", data);	//顯示10件商品
		int pagetotal = pagelogic.intValue();
		for (int x = 1; x <= pagetotal; x++) {	//頁碼[1,2,3...]
			pagetotals.add(x);
		}
		request.setAttribute("select", select);// 把搜尋條件補回前端頁面
		request.setAttribute("pageNo", pageNo);
		request.setAttribute("pagetotals", pagetotals);
		return mapping.findForward("queryGoods");
	}

}
