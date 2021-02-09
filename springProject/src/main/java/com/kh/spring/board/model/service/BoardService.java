package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.PageInfo;

public interface BoardService {

	/** 페이징 처리 객체 생성 Service
	 * @param type
	 * @param cp
	 * @return pInfo
	 */
	public abstract PageInfo getPageInfo(int type, int cp);

	/** 게시글 목록 조회 Service
	 * @param pInfo
	 * @return bList
	 */
	public abstract List<Board> selectList(PageInfo pInfo);

	/** 게시글 상세 조회 Service
	 * @param boardNo
	 * @param type 
	 * @return board
	 */
	public abstract Board selectBoard(int boardNo, int type);

	/** 게시글 등록(+ 파일 업로드) Service
	 * @param map
	 * @param savePath 
	 * @param images 
	 * @return
	 */
	public abstract int insertBoard(Map<String, Object> map, List<MultipartFile> images, String savePath);
	

}
