<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:url value="/js" var="JS_PATH" />
<c:url value="/" var="WEB_PATH" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>販賣機</title>
<script src="${JS_PATH}/jquery-1.11.1.min.js"></script>
<style type="text/css">
.product-description {
	pointer-events: none;
}

.page {
	display: inline-block;
	padding-left: 10px;
}
</style>
<script type="text/javascript">
var $overlay = null;
$(document).ready(function() {
	// 讓圖片在滑鼠移入時變暗
	$("body").on("mouseenter", "img:not(.no-effect)", function(e) {
	  // 調整圖片濾鏡效果讓圖片變暗
	  $(this).css({
	    "filter": "brightness(1)",
	    "transition": "filter 0.3s"
	  });
	  // 加上陰影讓圖片有立體感
	  $(this).css({
		  "box-shadow": "2px 2px 5px rgba(0, 0, 0, 0.5)",
		  "transition": "box-shadow 0.3s"
	  });
	  // 讓游標變成手指
	  $(this).css("cursor", "pointer");
	}).on("mouseleave", "img:not(.no-effect)", function(e) {
	  // 恢復圖片原本的樣子
	  $(this).css({
	    "filter": "",
	    "box-shadow": "",
	    "transition": ""
	  });
	  // 讓游標恢復原本的樣子
	  $(this).css("cursor", "");
	  }).on("click", "img:not(.no-effect)", function(e) {
		  
		  // 如果已經存在商品描述框元素，先移除它
		    if ($(".product-description").length > 0) {
		      $(".product-description").remove();
		    
		    }
	    // 取得商品描述文字
	    var description = $(this).data("description");
	    // 取得當前滑鼠所在的 img 元素
	    var $img = $(this);
	    // 取得 img 元素相對於 body 元素的位置
	    var position = $img.offset();
	    // 取得 img 元素的寬高
	    var width = $img.width();
	    var height = $img.height();
	    // 創建一個透明布幕
	    $("<div>")
	    .addClass("modal-backdrop")
	    .css({
	      "position": "fixed",
	      "top": 0,
	      "left": 0,
	      "width": "100%",
	      "height": "100%",
	      "background-color": "rgba(0, 0, 0, 0.5)",
// 	      z-index設的比商品描述框元素的z-index低，這樣布幕會在商品描述下層
	      "z-index": 1040
	    })
	    .appendTo("body");
	    // 創建一個商品描述框元素，並添加到 body 元素中
	    $("<div>")
    .addClass("product-description")
    .html("<div style='display:flex;'><div style='width:66.66%;'><img src='" + $img.attr("src") + "' style='width:100%;'></div><div style='width:33.33%;padding:10px;background-color:rgba(80,80,80,0.9);border-radius:5px;color:#fff'><h3>商品描述</h3><p>" + description + "</p></div></div>")
    .css({
    	"position": "absolute", // 設定元素的定位方式為絕對位置
    	"left": "50%", // 元素的左邊緣與父元素的左邊緣對齊
    	"top": "50%", // 元素的上邊緣與父元素的上邊緣對齊
    	"transform": "translate(-50%, -50%)", // 使元素相對於自身的中心點定位
    	"width": "600px", // 元素寬度為 600 像素
    	"padding": "2px", // 元素內容與邊框的距離為 2 像素
    	"background-color": "rgba(255,192,203,0.5)", // 設定背景顏色為淡粉色，透明度為 0.5
    	"box-shadow": "0px 0px 5px #888", // 加上陰影讓元素有立體感
    	"border": "2px solid #ff69b4", // 設定元素邊框為粉紅色實線，寬度為 2 像素
    	"border-radius": "5px", // 設定元素邊框的圓角半徑為 5 像素
    	"color": "#ff0000", // 設定文字顏色為紅色
    	"z-index": 1050 // 設定元素的 Z 軸順序為 1050，使元素浮在透明布幕元素(1040)上方

    })
    .appendTo("body");
	  
	    // 防止點擊事件冒泡到圖片上
	    e.stopPropagation();
	  });

	  // 點擊任何地方可以隱藏商品描述
	  $("body").on("click", ".product-description", function(e) {
	    $(this).remove();
	  }).on("click", function(e) {
	    $(".product-description").remove();
	    $(".modal-backdrop").remove();
	  });
	}); 


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
				<!-- 			<button onclick="CartGoodsview()">購物車商品列表</button> --> <a
				href="FrontendAction.do?action=CartGoodsview" align="left">購物車商品列表</a>
				<button onclick="clearCartGoods()">清空購物車</button>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="right">
				<form action="FrontendAction.do" method="get">
					<input type="hidden" name="action" value="VendingMachineview" /> <input
						type="hidden" name="pageNo" value="1" /> <input type="text"
						name="searchKeyword" value="${deta.searchKeyword}" /> <input
						type="submit" value="商品查詢" />
				</form>
			</td>
		</tr>
		<tr>
			<td width="400" height="200" align="center"><img border="0"
				src="DrinksImage/coffee.jpg" width="200" height="200"
				class="no-effect">
				<h1>歡迎光臨，${account.name}！</h1> <a
				href="BackendAction.do?action=Backend" align="left">後臺頁面</a>&nbsp;
				&nbsp; <a href="LoginAction.do?action=logout" align="left">登出</a> <br />
			<br /> <c:if test="${not empty buyGoodsMsg}">

					<div
						style="border-width: 3px; border-style: dashed; border-color: #FFAC55; padding: 5px; width: 300px;">
						<p style="color: blue;">~~~~~~~ 消費明細 ~~~~~~~</p>

						<c:forEach items="${buyGoodsMsg}" var="buyGoodsMsg">
						${buyGoodsMsg} <br />

						</c:forEach>

						<%
							session.removeAttribute("buyGoodsMsg");
						%>
					</div>
				</c:if></td>
			<td width="600" height="400">
				<table border="1" style="border-collapse: collapse">
					<tr>
						<c:forEach items="${goods}" var="good" begin="0" end="2" step="1">
							<td width="300"><font face="微軟正黑體" size="5"> <!-- 例如: 可口可樂 30元/罐 -->
									${good.goodsName}
							</font> <br /> <font face="微軟正黑體" size="4" style="color: gray;">
									<!-- 例如: 可口可樂 30元/罐 --> ${good.goodsPrice} 元/罐
							</font> <br /> <!-- 各商品圖片 --> <img border="0"
								src="DrinksImage/${good.goodsImageName}" width="150"
								height="150" data-description="${good.DESCRIPTIO}"> <br />
								<font face="微軟正黑體" size="3"> <input type="hidden"
									name="goodsID" value="1"> <!-- 設定最多不能買大於庫存數量 --> 購買<input
									type="number" name="buyQuantity" min="0" max="30" size="5"
									value="0">罐 <br>
								<br>
								<button
										onclick="addCartGoods(${good.goodsID},${goods.indexOf(good)})">加入購物車</button>
									<!-- 顯示庫存數量 --> <br>
								<p style="color: red;">(庫存 ${good.goodsQuantity} 罐)</p>
							</font></td>
						</c:forEach>

					</tr>
					<tr>
						<c:forEach items="${goods}" var="good" begin="3" end="5" step="1">
							<td width="300"><font face="微軟正黑體" size="5"> <!-- 例如: 可口可樂 30元/罐 -->
									${good.goodsName}
							</font> <br /> <font face="微軟正黑體" size="4" style="color: gray;">
									<!-- 例如: 可口可樂 30元/罐 --> ${good.goodsPrice} 元/罐
							</font> <br /> <!-- 各商品圖片 --> <img border="0"
								src="DrinksImage/${good.goodsImageName}" width="150"
								height="150" data-description="${good.DESCRIPTIO}"> <br />
								<font face="微軟正黑體" size="3"> <input type="hidden"
									name="goodsID" value="1"> <!-- 設定最多不能買大於庫存數量 --> 購買<input
									type="number" name="buyQuantity" min="0" max="30" size="5"
									value="0">罐 <br>
								<br>
								<button
										onclick="addCartGoods(${good.goodsID},${goods.indexOf(good)})">加入購物車</button>
									<!-- 顯示庫存數量 --> <br>
								<p style="color: red;">(庫存 ${good.goodsQuantity} 罐)</p>
							</font></td>
						</c:forEach>

					</tr>



				</table>
				<div id="product-description-box"
					style="position: absolute; display: none; background-color: black; color: yellow;"></div>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="right"><c:if
					test="${pagetotals.size()!=1}">

					<c:if test="${deta.pageNo>1}">
						<h3 class="page">
							<a
								href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${deta.pageNo-1}
	<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
	&amp;method=get">
								上一頁 </a>
						</h3>
					</c:if>

					<c:forEach items="${pages}" var="page">
						<h3 class="page">
							<a
								href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${page}
			<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
		&amp;method=get"
								<c:if test="${deta.pageNo == page}">style="color:red;"</c:if>>${page}</a>
						</h3>
					</c:forEach>


					<c:if
						test="${deta.pageNo != pagetotals.size() &&pagetotals.size()!=0}">
						<h3 class="page">
							<a
								href="FrontendAction.do?action=VendingMachineview&amp;pageNo=${deta.pageNo+1}
	<c:if test="${not empty deta.searchKeyword}">&amp;searchKeyword=${deta.searchKeyword}</c:if>
	&amp;method=get">
								下一頁 </a>
						</h3>
					</c:if>


				</c:if></td>
		</tr>
	</table>
	<%
		session.removeAttribute("data");
	%>
	<%
		session.removeAttribute("pagetotals");
	%>
	<%
		request.removeAttribute("data");
	%>
	<%
		request.removeAttribute("pagetotals");
	%>

</body>

</html>