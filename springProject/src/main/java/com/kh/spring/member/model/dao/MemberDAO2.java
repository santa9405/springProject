package com.kh.spring.member.model.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;

@Repository
public class MemberDAO2 {

	@Autowired
	private SqlSessionTemplate sqlSession;

	/** 아이디 중복 체크 DAO
	 * @param memberId
	 * @return result
	 */
	public int idDupCheck(String memberId) {
		return sqlSession.selectOne("memberMapper2.idDupCheck", memberId);
	}

	/** 회원 가입 DAO
	 * @param signUpMember
	 * @return result
	 */
	public int signUp(Member signUpMember) {
		return sqlSession.insert("memberMapper2.signUp", signUpMember);
	}

	/** 회원 정보 수정 DAO
	 * @param updateMember
	 * @return result
	 */
	public int updateAction(Member updateMember) {
		return sqlSession.update("memberMapper2.updateAction", updateMember);
	}

	/** 비밀번호 조회 DAO
	 * @param memberNo
	 * @return savePwd
	 */
	public String selectPwd(int memberNo) {
		return sqlSession.selectOne("memberMapper2.selectPwd", memberNo);
	}

	/** 비밀번호 변경 DAO
	 * @param map
	 * @return result
	 */
	public int updatePwd(Map<String, Object> map) {
		return sqlSession.update("memberMapper2.updatePwd", map);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
