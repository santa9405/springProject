package com.kh.spring.board.model.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.PageInfo;

@Repository // 저장소(DB) 연결 객체임을 알려줌 + bean 등록
public class BoardDAO {

	// 마이바티스를 이용한 DB 연결 객체 의존성 주입
	@Autowired
	private SqlSessionTemplate sqlSession; // root-context.xml에서 bean으로 등록돼있는 상태

	
	/** 특정 게시판 전체 게시글 수 조회 DAO
	 * @param type
	 * @return listCount
	 */
	public int getListCount(int type) {
		return sqlSession.selectOne("boardMapper.getListCount", type);
	}


	/** 게시글 목록 조회 DAO
	 * @param pInfo
	 * @return bList
	 */
	public List<Board> selectList(PageInfo pInfo) {
		
		// RowBounds 객체 : offset과 limit를 이용하여 조회 결과 중 일부 행만 조회하는
		//				    마이바티스 객체
		int offset = (pInfo.getCurrentPage() -1) * pInfo.getLimit();
		
		RowBounds rowBounds = new RowBounds(offset, pInfo.getLimit());
		
		return sqlSession.selectList("boardMapper.selectList", pInfo.getBoardType(), rowBounds);
	}


	/** 게시글 상세 조회 DAO
	 * @param temp
	 * @return board
	 */
	public Board selectBoard(Board temp) {
		return sqlSession.selectOne("boardMapper.selectBoard", temp);
	}


	/** 게시글 조회 수 증가 DAO
	 * @param boardNo
	 * @return result
	 */
	public int increaseReadCount(int boardNo) {
		return sqlSession.update("boardMapper.increaseReadCount", boardNo);
	}
	
}
