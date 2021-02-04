package com.kh.spring.member.model.service;

import com.kh.spring.member.model.vo.Member;

public interface MemberService2 {

	// 인터페이스 내에 작성되는 모든 필드는 public static final이다.
	// 인터페이스 내에 작성되는 모든 메소드는 묵시적으로 public abstract이다.
	
	/** 아이디 중복 검사 Service
	 * @param memberId
	 * @return result
	 */
	public abstract int idDupCheck(String memberId);

	
	/** 회원 가입 Service
	 * @param signUpMember
	 * @return result
	 */
	public abstract int signUp(Member signUpMember);

}
