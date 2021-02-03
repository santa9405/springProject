package com.kh.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.member.model.service.MemberService2;

@Controller
@RequestMapping("/member2/*")
public class MemberController2 {

	@Autowired // 해당 자료형과 일치하는 bean을 의존성 주입(DI)
	private MemberService2 service;
	
	
	// 회원가입 화면 전환용 Controller
	@RequestMapping("signUp")
	public String signUpView() {
		return "member/signUpView";
	}
	
	// 아이디 중복 체크 Controller(AJAX)
	@RequestMapping("idDupCheck")
	@ResponseBody
	public int idDupCheck(@RequestParam("memberId") String memberId) {
		
		//System.out.println(memberId);
		
		// 아이디 중복 검사 서비스 호출
		int result = service.idDupCheck(memberId);
		
		// 컨트롤러에서 반환되는 값은 forward 또는 redirect를 위한 경로/주소가 작성되는게 일반적
		// -> 컨트롤러에서 반환 시 Dispatcher Servlet으로 이동되어
		//	  View Resolver 또는 Handler Mapping으로 연결됨.
		
		// AJAX에서 반환값이 주소/경로가 아닌 값 자체로 인식해서 요청 부분으로 돌아가게 하는 별도의 방법이 존재
		// == @ResponseBody
		
		return result;
		
	}
	
}
