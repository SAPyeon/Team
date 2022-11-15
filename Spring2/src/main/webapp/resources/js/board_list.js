/**
 * 
 */
//글쓰기 버튼 클릭시 글쓰는 페이지 이동
$("#btn_Write").on("click",function(){
	location.href="/board/write"
})
//카테고리(타입) 버튼 클릭시  해당 카테고리(타입)만 보여주기, 이 떄 품절상품제외버튼과 정렬과 같이 연동
$(".category_type").on("click",function(){
	location.href=$(this).children().first().prop("href")
})
//셀렉트 박스 정렬
const searchValue = $("#searchValue").val();
//console.log(searchValue)
const caValue = $("#caValue").val();
const catValue = $("#catValue").val();
$("#array").on("change",function(){
	console.log(this.value)
	location.href=`list?pageNum=1&amount=12&search=${searchValue}&type=${$("#except").val()}&array=${this.value}&category_area=${caValue}&category_type=${catValue}`;
})

//천원단위 ,찍기
function priceToString(price) {
	return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

const price = document.querySelectorAll(".priceformat");

for(let p of price){
	p.innerText = priceToString(p.innerText);
}
//품절상품제외 체크박스
$("#ch_soldout").on("click",function(){
	const except = $("#except").val();
	if(except === "except"){
		location.href=`list?pageNum=1&amount=12&search=${searchValue}&type=&array=${$("#array").val()}&category_area=${caValue}&category_type=${catValue}`;	
	}else{
		location.href=`list?pageNum=1&amount=12&search=${searchValue}&type=except&array=${$("#array").val()}&category_area=${caValue}&category_type=${catValue}`;
	}
})

// 해당페이지번호 글자 굵게
$(".pageNumber").each(function(i,page){
	if(page.innerText == $("#currentPageNum").val()){
		$(this).css("font-weight","bold")
	}
})




