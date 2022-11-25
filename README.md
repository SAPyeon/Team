# Team



# 팀프로젝트
주제 : 가구제작 혹은 판매하는 쇼핑몰 사이트  
인원 : 4명  
기간 : 2022-08-04 ~ 2022-10-11  



### 기본셋팅  
---------------------------
eclipse  

Spring MVC  

Tomcat 8.5  

java8  

MySQL  

bootstrap  

JQeury  




​
### 맡은역할
------------------------
상품 리스트페이지 및 상품 상세페이지, 리뷰페이지
​

### 페이지 설명 
-----------------------------------
#### 1.상품 등록 페이지  
Controller : BoardController
 : /board/list

상품관리자만 등록 가능하게 하기

상품테이블 필수 목록 작성 : 상품이름, 가격, 소재, 재고숫자, 카테고리 번호 등등

목록리스트에 썸네일로 쓸 사진 등록, 첨부파일 등록 시 미리보기

내용 등록 시 사진과 글 내용이 한꺼번에 테이블에 저장하여 불러오기 - contenteditable사용

필요부분 미등록시 알림창을 띄우고 등록 막기

​

​


#### 2.상품 목록 리스트

사진과 함께 등록

품절 시 사진에 sold out 그림이 사진 대체 -  해당 상품 클릭 막기

품절상품제외 버튼 클릭시 품절 상품 제외하고 목록 보여주기

인기순, 판매순, 가격순 등 정렬할 수 있게 하기

12개씩 묶어서 페이징처리하기

카테고리 클릭 시 해당 카테고리에 등록된 상품만 보여주기


​

​


#### 3.상품상세페이지

해당 상품을 보여주고 구매하기로 바로 결제하기

장바구니 버튼 클릭시 해당 상품 장바구니목록에 넣기

찜 버튼 클릭 시 위시리스트에 담기(이후 버튼 색이 변하여 구분)

네비게이션 바가 보여지고 버튼 클릭 시 페이지 내에서 상세보기, 리뷰보기, 문의하기, 배송방법부분으로 이동

해당 상세 내용 아래에 관리자만 수정, 삭제버튼이 보여지고 게시물 수정,삭제하기

페이지 안에 리뷰리스트와 문의하기 리스트가 보여지게 함

​

​

​

#### 4.상품상세페이지 안 리뷰리스트

로그인을 안 할 시 '리뷰쓰기' 대신 '로그인하세요'가 나오고 버튼 클릭 시 로그인 페이지로 이동

로그인을 하고 구매자만 '리뷰쓰기' 글이 나오고 리뷰쓸 수 있게 하기

글쓴이 이름, 프로필 사진, 등록일, 리뷰사진 보여지기

리뷰 등록시 리뷰 점수마다  총 횟수를 불러오고 총 리뷰 평균 평점 등록하기

리뷰마다 리뷰평점 점수에 별표(★)로 보여지게 하기 

리뷰사진 클릭 시 팝업창으로 크게보여짐

해당 리뷰 작성자만 리뷰 수정, 삭제버튼이 보여지고 수정, 삭제

좋아요버튼이 있고 아이디마다 좋아요 버튼을 불러오고 한번 누르면 좋아요 , 한번 더 누르면 좋아요 취소

최신순, 베스트 순 버튼클릭시 정렬하기

페이징 처리하기- 10개 이상이면 다음버튼이 나와서 11페이지등록, 시작페이지가 1페이지가 아니면 이전버튼 나오기

​

​

​

#### 5. 리뷰쓰기페이지

리뷰 별표모양으로 점수 찍기

리뷰 내용, 사진 등록하기

사진 첨부파일 첨부 시 미리보기

폼 작성을 다 안할 시 알림창을 띄우고 등록이 안되도록 함

​

​



#### 6. 상품등록에 수정페이지, 리뷰쓰기에 수정페이지 작성

글쓰기 페이지와 동일하게 하고 해당 내용을 불러오기


​


#### 7.기타
헤더부분에 찾기폼에 입력하면 상품 내용이나 이름에 해당 문자를 가진 상품리스트가 보여짐
카테고리리스트가 보여짐(css, js 부분)



