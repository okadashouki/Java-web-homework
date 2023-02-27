<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:url value="/" var="WEB_PATH"/>
<c:url value="/js" var="JS_PATH"/>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>販賣機-後臺</title>
<script src="${JS_PATH}/jquery-1.11.1.min.js"></script>
<script type="text/javascript">

	$(document).ready(function(){
	$("#goodsID").bind("change",function(){
	var goodsID = $("#goodsID option:selected").val();
	if(goodsID != ""){
		$.ajax({
	 			 url: '${WEB_PATH}BackendAction.do?action=getModifygood', // 指定要進行呼叫的位址
	 			 type: "GET", // 請求方式 POST/GET
	  			 data: {id : goodsID}, // 傳送至 Server的請求資料(物件型式則為 Key/Value pairs)
	 			 dataType : 'JSON', // Server回傳的資料類型
	  			 success: function(goodInfo) { // 請求成功時執行函式
	  			 $("#Price").val(goodInfo.goodsPrice);
	  		 	$("#Quantity").val(goodInfo.goodsQuantity);
	  			},
	 		 error: function(error) { // 請求發生錯誤時執行函式
	     	alert("Ajax Error!");
	  								}
			});
					}else{
					$("#Price").val('');
  					$("#Quantity").val('');
				}
			});
		});	
</script>

</head>
<body>
<%@ include file="FM.jsp" %>
<br/><br/><HR>
		
	<h2>商品維護作業</h2><br/>
	<div style="margin-left:25px;">
	<form action="BackendAction.do?action=updateGoods" method="post">
	<p style="color:blue;">${sessionScope.updateGoodsMsg}</p>
	<% session.removeAttribute("updateGoodsMsg"); %>
		<p>
			飲料名稱：
			 <select size="1" id="goodsID" name="goodsID">
			<option value="">----- 請選擇 -----</option>
			<c:forEach items="${datas}" var="data">
					<option <c:if test="${data.goodsID eq updateGoods.goodsID}">selected</c:if> 
						value="${data.goodsID}">
						${data.goodsName}
					</option>
				</c:forEach>
			</select>
		</p>		
		<p>
			更改價格： 
			<input type="number" id="Price"name="goodsPrice" size="5" value="${updateGoods.goodsPrice}" min="0" max="1000">
		</p>
		<p>
			目前數量：
			<input  id="Quantity"  size="5" value="${updateGoods.goodsQuantity}" min="0" max="1000">
		</p>
		<p>
			補貨數量：
			<input type="number" id="Quantity"name="goodsQuantity" size="5" value="0" min="0" max="1000">
		</p>
		<p>
			商品狀態：
			<select name="status">
				<option value="1">上架</option>
				<option value="0">下架</option>				
			</select>
		</p>
		<p>
			<input type="submit" value="送出">
		</p>
		<% session.removeAttribute("modifyGoodID"); %>
	</form>
	</div>
	
</body>
</html>