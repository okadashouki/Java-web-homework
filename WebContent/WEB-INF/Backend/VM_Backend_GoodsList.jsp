<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>販賣機-後臺</title>
<style type="text/css">
		.page {
			display:inline-block;
			padding-left: 10px;
		}
	</style>
	<script type="text/javascript">
		
	</script>
</head>
<body>
<%@ include file="FM.jsp" %>
	<br/><br/><HR>		
<h2>商品資料查詢</h2><br/>
	<div style="margin-left:25px;">
	<form action="Selectfrom.do" method="get">
	<input type="hidden" name="action" value="Selectgood"/>
	
	<p>
			商品編號
			<input type="number" name="goodsID" size="5" value="0" min="0" max="1000"/>
		</p>
	<p>
			商品名稱(不分大小寫)
			<input type="text" name="goodsName" size="10"/>
		</p>
	<p>
			商品價格最低價
			<input type="text" name="minPrice" size="5"  min="0" max="1000"/>
		</p>
		<p>
			商品價格最高價
			<input type="text" name="maxPrice" size="5" min="0" max="1000"/>
		</p>
		
		商品價格排序
		<select name ="sort">
		<option value="0">請選擇</option>
		<option value="1">由最高至最低</option>
		<option value="2">由最低至最高</option>
		</select>
	<p>
			商品最低庫存數量
			<input type="number" name="goodsQuantity" size="5" value="0" min="0" max="1000"/>
		</p>
		<p>
			商品狀態：
			<select name="status">
				<option value="1">上架</option>
				<option value="2">下架</option>				
			</select>
		</p>
		<input type="hidden" name="pageNo" value="1"/>
		<p>
			<input type="submit" value="查詢">
		</p>
	</form>
	</div>
<h2>商品列表</h2><br/>
	<div style="margin-left:25px;">
	<table border="1">
		<tbody>
			<tr height="50">
				<td width="150"><b>商品名稱</b></td> 
				<td width="100"><b>商品價格</b></td>
				<td width="100"><b>現有庫存</b></td>
				<td width="100"><b>商品狀態</b></td>
			</tr>
			<c:forEach items="${data}" var="data">
			<tr height="30">
				<td>${data.goodsName}</td> 
				<td>${data.goodsPrice}</td>
				<td>${data.goodsQuantity}</td>
				<c:choose>
				<c:when test="${data.status ==1}"> 
				<td><p style="color:blue;">上架</p></td>		
				</c:when>
				<c:otherwise>
				 <td><p style="color:blue;">下架</p></td>	
				</c:otherwise>
				</c:choose> 	
			</tr>	
			</c:forEach>
			</tbody>
	</table>
<%-- 	  目前 ${pageNo}總頁數 ${pagetotals.size()} 下一頁 ${pageNo+1} --%>
	<td colspan="2" align="right">
   	<!-- 分頁 -->
   	<c:if test="${pagetotals.size()!=1}">
   	
   	<c:if test="${pageNo>1}">
	<h3 class="page"> <a href="Selectfrom.do?pageNo=${pageNo-1}&amp;action=Selectgood&amp;goodsID=&amp;goodsName=&amp;minPrice=&amp;
		maxPrice=&amp;sort=&amp;goodsQuantity=&amp;status=&amp;"> 上一頁 </a> </h3>
	</c:if>
	
	<c:forEach items ="${pagetotals}" var="pagetotal">
	<h3 class="page">
		<a href="Selectfrom.do?pageNo=${pagetotal}&amp;action=Selectgood&amp;goodsID=&amp;goodsName=&amp;minPrice=&amp;
		maxPrice=&amp;sort=&amp;goodsQuantity=&amp;status=&amp;" <c:if test="${pageNo == pagetotal}">style="color:red;"</c:if>>${pagetotal}</a> 
	</h3>
	</c:forEach>
	
	
	<c:if test="${pageNo != pagetotals.size() &&pagetotals.size()!=0}">
	<h3 class="page"> <a href="Selectfrom.do?pageNo=${pageNo+1}&amp;action=Selectgood&amp;goodsID=&amp;goodsName=&amp;minPrice=&amp;
		maxPrice=&amp;sort=&amp;goodsQuantity=&amp;status=&amp;"> 下一頁 </a> </h3>
	</c:if>
	

	</c:if>
	</td>	
	</div>
	<% request.removeAttribute("pageNo"); %>
	<% request.removeAttribute("data"); %>
</body>
</html>