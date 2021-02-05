package com.kh.spring.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDAO;
import com.kh.spring.member.model.vo.Member;

@Service // Service layer, 비즈니스 로직(데이터 가공, 트랜잭션 처리)을 가지는 클래스임을 명시 + bean등록
public class MemberServiceImpl implements MemberService {

	@Autowired // 자동으로 MemberDAO 객체(bean) 의존성 주입(DI)
	private MemberDAO dao;
	
	@Autowired
	private BCryptPasswordEncoder enc;
	
	// 로그인 Service
	@Override
	public Member loginAction(Member inputMember) {
		// Connection, SqlSession을 생성해서 DAO로 전달했던 이유 --> 트랜잭션 처리를 위해서
		// 하지만 Spring에서는 필요가 없음. AOP를 이용한 트랜잭션 처리 기술을 활용할 예정
		
		// DAO를 수행하고 결과를 반환 받음.
		Member loginMember = dao.loginAction(inputMember);
		
		// bcrypt 암호화를 사용하는 경우
		// 같은 비밀번호를 입력해도 암호화된 문자열이 계속 다르므로
		// DB에서 비밀번호 일치를 이용한 조건식 사용이 불가능 하다.
		// -> 대신 이를 비교할 수 있는 별도의 메소드(matches())를 BCryptPasswordEncoder가 제공해줌
		
		
		// inputMember에 저장된 비밀번호 : 123123
		
		// DB에 저장된 비밀번호
		//$2a$10$/MPJ5m2NqZDoDWALSg/jyueMrpAKBIgf0P.uSCoUZux4rt7.FjRpK
		
		if(loginMember != null) {
			
			// 비밀번호가 같을 때
			if(enc.matches(inputMember.getMemberPwd(), // 입력받은 평문 비밀번호
							loginMember.getMemberPwd())) { // DB에 저장된 암호화 비밀번호
				
				// DB에서 조회된 회원정보를 반환하면 되지만
				// 비밀번호는 null 값으로 변경해서 내보냄.
				loginMember.setMemberPwd(null);
				
			}else { // 비밀번호가 다를 때
				
				// 로그인이 실패한 모양을 만들어 줌
				loginMember = null;

			}
		}
		
		return loginMember;
	}

	
	
	
	
	
}
