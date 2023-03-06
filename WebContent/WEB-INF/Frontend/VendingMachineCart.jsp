<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>販賣機</title>
<style type="text/css">
		.page {
			display:inline-block;
			padding-left: 10px;
		}
	</style>
	<script type="text/javascript">
		function addCartGoods(goodsID, buyQuantityIdx){
			console.log("goodsID:", goodsID);			
			var buyQuantity = document.getElementsByName("buyQuantity")[buyQuantityIdx].value;
			console.log("buyQuantity:", buyQuantity);
			const formData = new FormData();
			formData.append('action', 'addCartGoods');
			formData.append('goodsID', goodsID);
			formData.append('buyQuantity', buyQuantity);
			// 送出商品加入購物車請求
			const request = new XMLHttpRequest();
			request.open("POST", "FrontendAction.do");			
			request.send(formData);
			request.onreadystatechange = function(){
				if (this.readyState == 4 && this.status == 200){
					var response = request.responseText;
					var responseJson = JSON.parse(response);
					alert(JSON.stringify(responseJson, null, 3));
				};
			}
		}
		function queryCartGoods(){
			const formData = new FormData();
			formData.append('action', 'queryCartGoods');
			// 送出查詢購物車商品請求
			const request = new XMLHttpRequest();
			request.open("POST", "FrontendAction.do");			
			request.send(formData);
			request.onreadystatechange = function(){
				if (this.readyState == 4 && this.status == 200){
					var response = request.responseText;
					var responseJson = JSON.parse(response);
					alert(JSON.stringify(responseJson, null, 3));
				};
			}
		}
		function clearCartGoods(){
			const formData = new FormData();
			formData.append('action', 'clearCartGoods');
			// 送出清空購物車商品請求
			const request = new XMLHttpRequest();
			request.open("POST", "FrontendAction.do");			
			request.send(formData);	
			request.onreadystatechange = function(){
				if (this.readyState == 4 && this.status == 200){
					var response = request.responseText;
					var responseJson = JSON.parse(response);
					alert(JSON.stringify(responseJson, null, 3));
				};
			}
		}
	</script>
</head>
<body>
<body align="center">
<table width="1000" height="400" align="center">
	<tr>
		<td colspan="2" align="right">
			<button onclick="queryCartGoods()">購物車商品列表</button>
			<button onclick="clearCartGoods()">清空購物車</button>			
		</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<form action="FrontendAction.do" method="get">
				<input type="hidden" name="action" value="VendingMachineview"/>
				<input type="hidden" name="pageNo" value="1"/>
				<input type="text" name="searchKeyword" value="${deta.searchKeyword}"/>
				<input type="submit" value="商品查詢"/>
			</form>
		</td>
	</tr>
	<tr>
		<td width="400" height="200" align="center">		
			<img border="0" src="DrinksImage/coffee.jpg" width="200" height="200" >			
			<h1>歡迎光臨，${account.name}！</h1>		
			<a href="BackendAction.do?action=Backend" align="left" >後臺頁面</a>&nbsp; &nbsp;
			<a href="LoginAction.do?action=logout" align="left">登出</a>
			<br/><br/>
			<form action="FrontendAction.do" method="post">
				<input type="hidden" name="action" value="buyGoods"/>				
				<font face="微軟正黑體" size="4" >
					<b>投入:</b>
					<input type="number" name="inputMoney" max="100000" min="0"  size="5" value="0">
					<b>元</b>		
					<b><input type="submit" value="送出">					
					<br/><br/>
				</font>
			</form>
			<c:if test="${not empty buyGoodsMsg}">
			
			<div style="border-width:3px;border-style:dashed;border-color:#FFAC55;
				padding:5px;width:300px;">
				<p style="color: blue;">~~~~~~~ 消費明細 ~~~~~~~</p>
				
					<c:forEach items="${buyGoodsMsg}" var="buyGoodsMsg">
						${buyGoodsMsg} <br/>
						
					</c:forEach>
					
					<% session.removeAttribute("buyGoodsMsg"); %>
			</div>	
			</c:if>
			
		</td>
		<td width="600" height="400">			
			<table border="1" style="border-collapse: collapse">
			<tr>
			<c:forEach items="${goods}" var="good" begin="0" end="2" step="1">
			<td width="300">							
						<font face="微軟正黑體" size="5" >
							<!-- 例如: 可口可樂 30元/罐 -->	
							${good.goodsName}
						</font>
						<br/>
						<font face="微軟正黑體" size="4" style="color: gray;" >
							<!-- 例如: 可口可樂 30元/罐 -->	
							${good.goodsPrice} 元/罐  
						</font>
						<br/>
						<!-- 各商品圖片 -->
						<img border="0" src="DrinksImage/${good.goodsImageName}" width="150" height="150" >						
						<br/>
						<font face="微軟正黑體" size="3">
							<input type="hidden" name="goodsID" value="1">
							<!-- 設定最多不能買大於庫存數量 -->
							購買<input type="number" name="buyQuantity" min="0" max="30" size="5" value="0">罐
							<br><br><button onclick="addCartGoods(${good.goodsID},${goods.indexOf(good)})">加入購物車</button>
							<!-- 顯示庫存數量 -->
							<br><p style="color: red;">(庫存 ${good.goodsQuantity} 罐)</p>
						</font>
					</td>				
			</c:forEach>
			
			</tr>
			<tr>
				<c:forEach items="${goods}" var="good" begin="3" end="5" step="1">
			<td width="300">							
						<font face="微軟正黑體" size="5" >
							<!-- 例如: 可口可樂 30元/罐 -->	
							${good.goodsName}
						</font>
						<br/>
						<font face="微軟正黑體" size="4" style="color: gray;" >
							<!-- 例如: 可口可樂 30元/罐 -->	
							${good.goodsPrice} 元/罐  
						</font>
						<br/>
						<!-- 各商品圖片 -->
						<img border="0" src="DrinksImage/${good.goodsImageName}" width="150" height="150" >						
						<br/>
						<font face="微軟正黑體" size="3">
							<input type="hidden" name="goodsID" value="1">
							<!-- 設定最多不能買大於庫存數量 -->
							購買<input type="number" name="buyQuantity" min="0" max="30" size="5" value="0">罐
							<br><br><button onclick="addCartGoods(${good.goodsID},${goods.indexOf(good)})">加入購物車</button>
							<!-- 顯示庫存數量 -->
							<br><p style="color: red;">(庫存 ${good.goodsQuantity} 罐)</p>
						</font>
					</td>				
			</c:forEach>
			
			</tr>
			
			
			
			</table>
		</td>		
	</tr>
	<tr>
		<td colspan="2" align="right">				
				<c:if test="${pagetotals.size()!=1}">
   	
   	<c:if test="${deta.pageNo>1}">
	<h3 class="page"> <a href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${deta.pageNo-1}
	<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
	&amp;method=get"> 上一頁 </a> </h3>
	</c:if>
	
	<c:forEach items ="${pages}" var="page">
	<h3 class="page">
		<a href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${page}
			<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
		&amp;method=get"<c:if test="${deta.pageNo == page}">style="color:red;"</c:if>>${page}</a> 
	</h3>
	</c:forEach>
	
	
	<c:if test="${deta.pageNo != pagetotals.size() &&pagetotals.size()!=0}">
	<h3 class="page"> <a href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${deta.pageNo+1}
	<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
	&amp;method=get"> 下一頁 </a> </h3>
	</c:if>
	
	
	</c:if>
	
		</td>
	</tr>
</table>
	<% session.removeAttribute("data"); %>
	<% session.removeAttribute("pagetotals"); %>
	<% request.removeAttribute("data"); %>
	<% request.removeAttribute("pagetotals"); %>

</body>

</html>