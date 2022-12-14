package org.spring2.mapper;

import java.util.ArrayList;

import org.spring2.model.BoardVO;
import org.spring2.model.CriteriaVO;
import org.spring2.model.OrderVO;
import org.spring2.model.RICriteriaVO;
import org.spring2.model.ReviewLikeVO;
import org.spring2.model.ReviewVO;


public interface ReviewMapper {
	
	// 리뷰 리스트 in 디테일 DB설계
	public ArrayList<ReviewVO> Rlist(int pno);
	
	// 리뷰 리스트 DB설계
	public ArrayList<ReviewVO> list(CriteriaVO cri);
	
	// 리뷰 전체건수 DB설계
	public int total(CriteriaVO cri);
	
	// 리뷰 작성 DB설계
	public void write(ReviewVO rvo);
	
	// 상품정보 가져오는 DB설계
	public BoardVO pro(CriteriaVO cri);
	
	// 이미지 업로드 DB설계
//	public void uploadimg(ReviewVO rvo);
	
	// 리뷰 좋아요 여부 DB설계
	public int findLike(ReviewLikeVO rvo);

	public int likeAdd(ReviewLikeVO rvo);

	public int likeRemove(ReviewLikeVO rvo);

	public int likeUpdate(ReviewVO rvo);
	
	//리뷰 삭제 DB설계
	public int remove(ReviewVO rvo); 
	
	//리뷰 수정 DB설계
	public ReviewVO findReview(ReviewVO rvo);

	public void modify(ReviewVO rvo);

	public ArrayList<OrderVO> reviewable(OrderVO ovo);
}
