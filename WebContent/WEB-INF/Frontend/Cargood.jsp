<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.Map" %>
<c:url value="/js" var="JS_PATH"/>
<c:url value="/" var="WEB_PATH"/>

<html>
<head>
 

    <title>購物車</title>
    <script src="${JS_PATH}/jquery-1.11.1.min.js"></script>
   
    <script type="text/javascript">
    function removeItem(goodsId) {
        if (confirm("確定要刪除此商品嗎？")) {
        	window.location.href = "FrontendAction.do?action=removeItem&goodsID=" + goodsId;

        }
    }
    $(document).on('change', '.quantity-input', function() {
        // 取得商品 ID
        var goodsId = $(this).closest('tr').data('goods-id');
        // 取得新的數量
        var newQuantity = $(this).val();

        // 保存當前的作用域，以便在成功回调函数中使用
        var $this = $(this);

        // 使用 Ajax 發送請求，更新購物車中該商品的數量
        $.ajax({
            url: '${WEB_PATH}FrontendAction.do?action=updateItem',
            type: 'POST',
            data: {
                goodsId: goodsId,
                newQuantity: newQuantity
            },
            success: function(response) {
            	console.log("成功");
            	console.log(response);
            	console.log(response[0].subTotal)
            	var targetData = null;
  			  for (var i = 0; i < response.length; i++) {
       				 if (response[i].goodsID == goodsId) {
           			 targetData = i;
          		  break;
       			 }
   			 }
  			console.log("這是第"+targetData);
                // 更新總金額與小計
                var subTotal = response[targetData].subTotal;
                // 使用之前保存的作用域
                $this.closest('tr').find('.sub-total').text(subTotal);
                var allSubTotal = 0;
                $('.sub-total').each(function() {
                  allSubTotal += parseInt($(this).text());
                });
                // 更新總金額欄位
                $("#total").text("總金額："+allSubTotal);
                
            },
            error: function() {
            	
            	  alert('更新數量失敗');
            	}
        });
    });

</script>
</head>
<body>
    <h1>購物車商品</h1>
    <hr>

    <%-- 檢查購物車是否有商品 --%>
    <c:if test="${not empty carGoods}">
<table border="1">
    <thead>
        <tr>
            <th>商品照片</th>
            <th>商品名稱</th>
            <th>商品價格</th>
            <th>購買數量</th>
            <th>商品總金額</th>
            <th>刪除商品</th>
        </tr>
    </thead>
    <tbody>
        <%-- 顯示購物車商品 --%>
        <c:set var="total" value="0" />
        <c:forEach items="${carGoods}" var="entry">
            <c:set var="goods" value="${entry.key}" />
            <c:set var="quantity" value="${entry.value}" />
            <c:set var="subTotal" value="${goods.goodsPrice * quantity}" />
            <c:set var="total" value="${total + subTotal}" />

              <tr data-goods-id="${goods.goodsID}">
                <td><img src="DrinksImage/${goods.goodsImageName}"width="50" height="50"></td>
                <td>${goods.goodsName}</td>
                <td>${goods.goodsPrice}</td>
               <td>
  <input class="quantity-input" type="number"  name="quantity" min="1" max ="${goods.goodsQuantity}" value="${quantity}">
  <br>
  <span style="color: red;">庫存: ${goods.goodsQuantity}</span>
  </td>
                <td class="sub-total" id="subTotal">${subTotal}</td>
                <td><button onclick="removeItem(${goods.goodsID})">刪除</button></td>
            </tr>
        </c:forEach>
    </tbody>
    <tfoot>
        <tr>
            <td id ="total" colspan="4" style="color: red;">總金額： ${total}</td>
        
        </tr>
    </tfoot>
</table>


    <br>
    <form action="FrontendAction.do" method="post">
				<input type="hidden" name="action" value="buyGoods"/>				
				<font face="微軟正黑體" size="4" >
					<b>投入:</b>
					<input type="number" name="inputMoney" max="100000" min="0"  size="5" value="0">
					<b>元</b>		
					<b><input type="submit" value="送出">					
					<br/><br/>
				</font>
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
    </c:if>

    <%-- 購物車沒商品 --%>
    <c:if test="${empty carGoods}">
        <p>購物車沒商品</p>
    </c:if>

    <br>
				<a href="FrontendAction.do?action=VendingMachine&amp;pageNo=1&amp;searchKeyword=">回販賣機</a>
			
</body>
</html> 