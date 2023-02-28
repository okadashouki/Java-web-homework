<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Vending Machine Backend Service</h1>
<br/>
	
	<table border="1" style="border-collapse:collapse;;margin-left:25px;">
		<tr>
			<td width="200" height="50" align="center">
				<a href="BackendAction.do?action=Backend">商品列表</a>
			</td>
			<td width="200" height="50" align="center">
				<a href="BackendAction.do?action=updateGoodsview">商品維護作業</a>
			</td>
			<td width="200" height="50" align="center">
				<a href="BackendAction.do?action=addGoodsview">商品新增上架</a>
			</td>
			<td width="200" height="50" align="center">
				<a href="BackendAction.do?action=querySalesReportview">銷售報表</a>
			</td>
			<td width="200" height="50" align="center">
				<a href="FrontendAction.do?action=VendingMachine&amp;pageNo=1">回販賣機</a>
			</td>
		</tr>
</table>