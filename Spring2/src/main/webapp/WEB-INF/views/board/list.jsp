<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>shopping</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Nanum+Gothic&display=swap" rel="stylesheet">
<link rel="stylesheet" href="../resources/css/board_list.css"
	type="text/css">
<link rel="stylesheet" href="../resources/css/home.css">
</head>
<body>
	<div id="entry_area">
		<%@ include file="../header.jsp"%>
		<div id="content_area">
			<div id="container">
				<c:if test="${caa.cname eq null }">
					<h1>모든제품</h1>
				</c:if>
				<h1>${caa.cname}</h1>
				<c:if test="${userInfo.admin eq true}">
					<div id="p_write">
						<button id="btn_Write">상품등록</button>
					</div>
				</c:if>

				<div id="p_list">
					<ul>
						<c:forEach items="${category}" var="category">
							<li class="category_type"><a
								${category.cno == paging.cri.category_type ? 'style ="color:red" ' :''}
								href="/board/list?category_area=${paging.cri.category_area}&category_type=${category.cno}">${category.cname}</a></li>
						</c:forEach>
					</ul>
				</div>
				<div id="chAndSelect">
					<div id="divExcept">
					<input type="checkbox" id="ch_soldout"
						${paging.cri.type =='except'? 'checked=checked':'' }> <label>품절상품제외</label>
					<input type="hidden" id="except" value="${paging.cri.type}">
					</div>
					<select id="array">
						<option value="popular"
							${paging.cri.array == 'popular' ? 'selected="selected"' : '' }>인기순</option>
						<option value="last"
							${paging.cri.array == 'last' ? 'selected="selected"' : '' }>최근등록순</option>
						<option value="sell"
							${paging.cri.array == 'sell' ? 'selected="selected"' : '' }>판매량많은순</option>
						<option value="lowprice"
							${paging.cri.array == 'lowprice' ? 'selected="selected"' : '' }>가격낮은순</option>
						<option value="highprice"
							${paging.cri.array == 'highprice' ? 'selected="selected"' : '' }>가격높은순</option>

					</select>
				</div>
				<%-- ${paging.cri.array} --%>
				<input type="hidden" id="searchValue" value="${paging.cri.search}">
				<input type="hidden" id="catValue"
					value="${paging.cri.category_type}"> <input type="hidden"
					id="caValue" value="${paging.cri.category_area}">
				<%-- ${paging.cri.search} --%>
				<div id="list">
					<ul>
						<c:forEach items="${list}" var="boardlist">
							<li><a href="/board/detail?pno=${boardlist.pno}">
									<div id="${boardlist.pno}" class="imglist">
										<input class="pno" type="hidden" value="${boardlist.pno}">
										<input type="hidden" value="${boardlist.category_area}"	name="category_area">

									</div> <input type="hidden" value="${boardlist.quantity}"
									class="quantity"> <input type="hidden"
									value="${boardlist.s_quantity}" class="s_quantity">

									<p>${boardlist.pname}</p>
									<p>
										가격 : <span class="priceformat">${boardlist.price} 원</span>
									</p> <input type="hidden" value="${boardlist.pno}" name="pno"></li>
							</a>
							</li>
						</c:forEach>
					</ul>
				</div>
				<div id="paging">
					 <input type="hidden" value="${paging.cri.pageNum}" id="currentPageNum">  
					 
					<!-- prev(이전)이 true이면 이전버튼 활성화 -->
					<c:if test="${paging.prev}">
						<a
							href="/board/list?pageNum=${paging.startPage-1}&amount=${paging.cri.amount}&search=${paging.cri.search}&type=${paging.cri.type}&array=${paging.cri.array}&category_area=${paging.cri.category_area}&category_type=${paging.cri.category_type}">이전</a>
					</c:if>
					<c:forEach begin="${paging.startPage}" end="${paging.endPage}"
						var="num">
						<a
							href="/board/list?pageNum=${num}&amount=${paging.cri.amount}&search=${paging.cri.search}&type=${paging.cri.type}&array=${paging.cri.array}&category_area=${paging.cri.category_area}&category_type=${paging.cri.category_type}" class="pageNumber">${num}</a>
					</c:forEach>
					<!-- next(다음)이 true이면 다음버튼 활성화 -->
					<c:if test="${paging.next}">
						<a
							href="/board/list?pageNum=${paging.endPage+1}&amount=${paging.cri.amount}&search=${paging.cri.search}&type=${paging.cri.type}&array=${paging.cri.array}&category_area=${paging.cri.category_area}&category_type=${paging.cri.category_type}">다음</a>
					</c:if>
				</div>

			</div>
		</div>
		<%@ include file="../footer.jsp"%>
	</div>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script src="../resources/js/home.js"></script>
	<script src="../resources/js/board_list.js"></script>
	<script src="../resources/js/imageAttach.js"></script>
</body>
</html>