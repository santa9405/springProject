package com.kh.spring.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDAO;
import com.kh.spring.member.model.vo.Member;

@Service // Service layer, 비즈니스 로직(데이터 가공, 트랜잭션 처리)을 가지는 클래스임을 명시 + bean등록
public class MemberServiceImpl implements MemberService {

	@Autowired // 자동으로 MemberDAO 객체(bean) 의존성 주입(DI)
	private MemberDAO dao;
	
	// 로그인 Service
	@Override
	public Member loginAction(Member inputMember) {
		// Connection, SqlSession을 생성해서 DAO로 전달했던 이유 --> 트랜잭션 처리를 위해서
		// 하지만 Spring에서는 필요가 없음. AOP를 이용한 트랜잭션 처리 기술을 활용할 예정
		
		// DAO를 수행하고 결과를 반환 받음.
		Member loginMember = dao.loginAction(inputMember);
		
		return loginMember;
	}

}
