package com.kh.spring.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.service.MemberServiceImpl;
import com.kh.spring.member.model.vo.Member;

//@Component // 객체(컴포넌트)를 나타내는 일반적인 타입으로 bean 등록 역할을 함
@Controller // 프레젠테이션 레이어, 웹 애플리케이션에서 전달된 요청 응답을 처리하는 클래스 + bean 등록
@RequestMapping("/member/*")
@SessionAttributes({"loginMember"}) // Model에 추가된 데이터 중 key 값이 해당 어노테이션에 적혀있는 값과 일치하는 데이터를 session scope로 이동
public class MemberController {
	
	// Spring 이전에는 service를 컨트롤러 내에서 공용으로 사용하기 위하여
	// 필드 또는 최상단 부분에 service 객체를 생성했지만
	//private MemberService service = new MemberServiceImpl();
	
	// Spring에서는 객체 생명 주기를 Spring Container가 관리할 수 있도록 함
	// == bean으로 등록하여 IOC를 통해 제어
	
	// @Autowired : 빈 스캐닝(component-scan)을 통해 등록된 bean 중 알맞은 bean을 해당 변수에 의존성 주입(DI)을 진행함
	@Autowired
	private MemberService service;

	/** 로그인 화면 전환용 Controller
	 * @return viewName
	 */
	@RequestMapping("login")
	public String loginView() {
		return "member/login";
	}
	
	// ---------------------------------------------------------------------
	// 로그인 동작 Controller
	
	// ***** 1. HttpServletRequest를 이용한 파라미터 전달 받기
	/*@RequestMapping("loginAction")
	public String loginAction(HttpServletRequest request) {
		// 매개변수에 HttpServletRequest를 작성한 경우
		// 해당 객체를 스프링 컨테이너가 자동으로 주입
		
		String memberId = request.getParameter("memberId");
		String memberPwd = request.getParameter("memberPwd");
		
		System.out.println("memberId : " + memberId);
		System.out.println("memberPwd : " + memberPwd);
		
		return "redirect:/";
	}*/
	
	// ***** 2. @RequestParam을 이용한 파라미터 전달 방법
	// - request객체를 이용한 파라미터 전달 어노테이션
	// - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨.
	
	// [속성]
	// value : 전달 받은 input 태그의 name 속성 값
	// required : 입력된 name 속성값 파라미터 필수 여부 지정(기본값 true)
	// -> required = true인 파라미터가 존재하지 않는다면 400 Bad Request 에러 발생
	// defaultValue : 파라미터 중 일치하는 name 속성 값이 없을 경우에 대입할 값 지정
	// -> required = false인 경우 주로 사용
	/*@RequestMapping(value = "loginAction", method = RequestMethod.POST)
	public String loginAction( @RequestParam("memberId") String memberId,
							   @RequestParam("memberPwd") String memberPwd,
							   @RequestParam(value = "cp", required = false, defaultValue = "1") int cp
							  ) {
		
		System.out.println("memberId : " + memberId);
		System.out.println("memberPwd : " + memberPwd);
		System.out.println("cp : " + cp);
		
		return "redirect:/";
	}*/
	
	// ***** 3. @RequestParam 어노테이션 생략
	// - 매개변수명을 전달되는 파라미터 name 속성값과 일치 시키면 자동으로 주입
	//   -> 어노테이션 코드를 생략할 경우 협업 시 가독성이 떨어짐
	/*@RequestMapping("loginAction")
	public String loginAction(String memberId, String memberPwd) {
		System.out.println("memberId : " + memberId);
		System.out.println("memberPwd : " + memberPwd);
		
		return "redirect:/";
	}*/
	
	// ***** 4. @ModelAttribute를 이용한 파라미터 전달
	// 요청 페이지에서 여러 파라미터가 전달 될 때
	// 해당 파라미터가 한 객체의 필드명과 같다면 
	// 일치하는 객체를 하나 생성하여 자동으로 세팅 후 반환
	
	// (주의사항) 전달 받아 값을 세팅할 VO 내부에는 반드시 기본생성자, setter가 작성되어 있어야 함!!
	// + name 속성값과 필드명이 같아야함!!
	
	// @ModelAttribute == 커맨드 객체
	/*@RequestMapping("loginAction")
	public String loginAction(@ModelAttribute Member inputMember) {
		System.out.println("memberId : " + inputMember.getMemberId());
		System.out.println("memberPwd : " + inputMember.getMemberPwd());
		
		return "redirect:/";
	}*/
	
	// ***** 5. @ModelAttribute 어노테이션 생략
	@RequestMapping("loginAction")
	public String loginAction(Member inputMember, Model model /*HttpSession session*/) {
		// inputMember -> memberId, memberPwd
		//System.out.println(inputMember);
		
		// 비즈니스 로직 수행 후 결과 반환받기
		Member loginMember = service.loginAction(inputMember);
		System.out.println(loginMember); // 결과 확인용
		
		//session.setAttribute("loginMember", loginMember);
		
		// Model : 데이터를 맵 형식(K : V) 형태로 담아서 전달하는 용도의 객체
		// Model 객체는 기본적으로 request scope 이지만
		// 클래스 위쪽에 작성된 @SessionAttributes를 이용하면 session scope로 변경됨
		
		if(loginMember != null){ // 로그인 성공 시
			model.addAttribute("loginMember", loginMember);
		}
		
		return "redirect:/";
	}
	
}









