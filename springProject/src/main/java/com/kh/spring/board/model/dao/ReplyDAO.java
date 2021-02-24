package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Reply;

@Repository // 저장소 관련 + Bean 등록
public class ReplyDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	/** 댓글 목록 조회 DAO
	 * @param parentBoardNo
	 * @return rList
	 */
	public List<Reply> selectReplyList(int parentBoardNo) {
		return sqlSession.selectList("replyMapper.selectReplyList", parentBoardNo);
	}

	/** 댓글 삽입 DAO
	 * @param map
	 * @return result
	 */
	public int insertReply(Map<String, Object> map) {
		return sqlSession.insert("replyMapper.insertReply", map);
	}

	/** 댓글 수정 DAO
	 * @param map
	 * @return result
	 */
	public int updateReply(Map<String, Object> map) {
		//return sqlSession.update("replyMapper.updateReply", reply);
		return sqlSession.update("replyMapper.updateReply", map);
	}

	/** 댓글 삭제 DAO
	 * @param replyNo
	 * @return result
	 */
	public int deleteReply(int replyNo) {
		return sqlSession.update("replyMapper.deleteReply", replyNo);
	}

	/** 답글 삽입 DAO
	 * @param map
	 * @return result
	 */
	public int insertChildReply(Map<String, Object> map) {
		return sqlSession.insert("replyMapper.insertChildReply", map);
	}
	
}
