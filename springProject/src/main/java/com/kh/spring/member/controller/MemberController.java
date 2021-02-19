package com.kh.spring.member.controller;

import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.service.MemberServiceImpl;
import com.kh.spring.member.model.vo.Member;

//@Component // 객체(컴포넌트)를 나타내는 일반적인 타입으로 bean 등록 역할을 함
@Controller // 프레젠테이션 레이어, 웹 애플리케이션에서 전달된 요청 응답을 처리하는 클래스 + bean 등록
@RequestMapping("/member/*")
@SessionAttributes({ "loginMember" }) // Model에 추가된 데이터 중 key 값이 해당 어노테이션에 적혀있는 값과 일치하는 데이터를 session scope로 이동
public class MemberController {

	// Spring 이전에는 service를 컨트롤러 내에서 공용으로 사용하기 위하여
	// 필드 또는 최상단 부분에 service 객체를 생성했지만
	// private MemberService service = new MemberServiceImpl();

	// Spring에서는 객체 생명 주기를 Spring Container가 관리할 수 있도록 함
	// == bean으로 등록하여 IOC를 통해 제어

	// @Autowired : 빈 스캐닝(component-scan)을 통해 등록된 bean 중 알맞은 bean을 해당 변수에 의존성
	// 주입(DI)을 진행함
	@Autowired
	private MemberService service;
	
	// Logger 객체 생성 : 로그를 작성할 수 있는 객체
	private Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	/**
	 * 로그인 화면 전환용 Controller
	 * 
	 * @return viewName
	 */
	@RequestMapping("login")
	public String loginView() {
		
		logger.debug("로그인 화면으로 전환됨(debug)");
		logger.info("로그인 화면으로 전환됨(info)");
		
		
		
		return "member/login";
	}

	// ---------------------------------------------------------------------
	// 로그인 동작 Controller

	// ***** 1. HttpServletRequest를 이용한 파라미터 전달 받기
	/*
	 * @RequestMapping("loginAction") public String loginAction(HttpServletRequest
	 * request) { // 매개변수에 HttpServletRequest를 작성한 경우 // 해당 객체를 스프링 컨테이너가 자동으로 주입
	 * 
	 * String memberId = request.getParameter("memberId"); String memberPwd =
	 * request.getParameter("memberPwd");
	 * 
	 * System.out.println("memberId : " + memberId);
	 * System.out.println("memberPwd : " + memberPwd);
	 * 
	 * return "redirect:/"; }
	 */

	// ***** 2. @RequestParam을 이용한 파라미터 전달 방법
	// - request객체를 이용한 파라미터 전달 어노테이션
	// - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입됨.

	// [속성]
	// value : 전달 받은 input 태그의 name 속성 값
	// required : 입력된 name 속성값 파라미터 필수 여부 지정(기본값 true)
	// -> required = true인 파라미터가 존재하지 않는다면 400 Bad Request 에러 발생
	// -> required = true인 파라미터가 null인 경우 400 Bad Request 에러 발생
	// defaultValue : 파라미터 중 일치하는 name 속성 값이 없을 경우에 대입할 값 지정
	// -> required = false인 경우 주로 사용
	/*
	 * @RequestMapping(value = "loginAction", method = RequestMethod.POST) public
	 * String loginAction( @RequestParam("memberId") String memberId,
	 * 
	 * @RequestParam("memberPwd") String memberPwd,
	 * 
	 * @RequestParam(value = "cp", required = false, defaultValue = "1") int cp ) {
	 * 
	 * System.out.println("memberId : " + memberId);
	 * System.out.println("memberPwd : " + memberPwd); System.out.println("cp : " +
	 * cp);
	 * 
	 * return "redirect:/"; }
	 */

	// ***** 3. @RequestParam 어노테이션 생략
	// - 매개변수명을 전달되는 파라미터 name 속성값과 일치 시키면 자동으로 주입
	// -> 어노테이션 코드를 생략할 경우 협업 시 가독성이 떨어짐
	/*
	 * @RequestMapping("loginAction") public String loginAction(String memberId,
	 * String memberPwd) { System.out.println("memberId : " + memberId);
	 * System.out.println("memberPwd : " + memberPwd);
	 * 
	 * return "redirect:/"; }
	 */

	// ***** 4. @ModelAttribute를 이용한 파라미터 전달
	// 요청 페이지에서 여러 파라미터가 전달 될 때
	// 해당 파라미터가 한 객체의 필드명과 같다면
	// 일치하는 객체를 하나 생성하여 자동으로 세팅 후 반환

	// (주의사항) 전달 받아 값을 세팅할 VO 내부에는 반드시 기본생성자, setter가 작성되어 있어야 함!!
	// + name 속성값과 필드명이 같아야함!!

	// @ModelAttribute == 커맨드 객체
	/*
	 * @RequestMapping("loginAction") 
	 * public String loginAction(@ModelAttribute Member inputMember) { 
	 * System.out.println("memberId : " + inputMember.getMemberId()); 
	 * System.out.println("memberPwd : " + inputMember.getMemberPwd());
	 * 
	 * return "redirect:/"; 
	 * 
	 * }
	 */

	// RedirectAttributes : redirect시 값을 전달하는 용도로 사용할 수 있는 객체

	// 응답 전 : request scope
	// redirect 중 : session scope
	// 응답 후 : request scope

	// addFlashAttributes()를 사용해서 값을 세팅

	// ***** 5. @ModelAttribute 어노테이션 생략
	@RequestMapping("loginAction")
	public String loginAction(Member inputMember, @RequestParam(value = "saveId", required = false) String saveId,
			HttpServletResponse response, RedirectAttributes ra, Model model /* HttpSession session */) {
		// inputMember -> memberId, memberPwd
		// System.out.println(inputMember);

		// 비즈니스 로직 수행 후 결과 반환받기
		Member loginMember = service.loginAction(inputMember);
		// System.out.println(loginMember); // 결과 확인용

		// session.setAttribute("loginMember", loginMember);

		// Model : 데이터를 맵 형식(K : V) 형태로 담아서 전달하는 용도의 객체
		// Model 객체는 기본적으로 request scope 이지만
		// 클래스 위쪽에 작성된 @SessionAttributes를 이용하면 session scope로 변경됨

		String url = null; // 로그인 성공 또는 실패 시의 요청 경를 저장할 변수

		if (loginMember != null) { // 로그인 성공 시
			model.addAttribute("loginMember", loginMember);

			// 쿠키 생성
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());

			// 쿠키 유지 시간 지정
			if (saveId != null) { // 아이디 저장이 체크 되었을 경우
				// 한 달 동안 유지되는 쿠키 생성
				cookie.setMaxAge(60 * 60 * 24 * 30); // 초 단위로 지정
			} else { // 아이디 저장이 체크 되어있지 않을 경우
				cookie.setMaxAge(0);
				// - 쿠키를 생성하지 않겠다.
				// - 기존에 있던 쿠키도 없애겠다.
			}

			// 생성된 쿠키 객체를 응답객체에 담아서 내보내기
			response.addCookie(cookie);

			url = "/"; // 성공 시 메인페이지

		} else { // 로그인 실패 시
			ra.addFlashAttribute("swalIcon", "error");
			ra.addFlashAttribute("swalTitle", "로그인 실패");
			ra.addFlashAttribute("swalText", "아이디 또는 비밀번호가 일치하지 않습니다.");

			url = "login"; // 로그인 실패 시 다시 로그인 화면으로
		}

		return "redirect:" + url;
	}

	// logout Controller
	@RequestMapping("logout")
	public String logout(SessionStatus status /* HttpSession session */) {
		// 로그아웃 : Session에 있는 회원 정보를 없애거나 만료시키는 것

		// 기존 방법 : HttpSession의 invalidate() 메소드를 이용해 세션 무효화를 진행
		// session.invalidate();

		// loginMember 세션을 어떻게 등록했는가?
		// model.addAttribute("loginMember", loginMember)
		// -> Model을 이용해서 값을 추가 + @SessionAttributes
		// --> 해당 방법으로 등록된 세션은 기존 방법으로 없애거나 만료시킬 수 없음.
		// --> SessionStatus 객체를 이용해야만 만료 가능.

		// SessionStatus : 세션의 상태를 관리할 수 있는 객체
		// setComplete() : @SessionAttributes로 Session에 등록된 값을 모두 지움.
		status.setComplete();

		return "redirect:/"; // 메인 화면 재요청
	}

	// 스프링 예외 처리 방법
	// 1) 메소드 별로 예외 처리 : try-catch, throws

	// 2) 컨트롤러 별로 예외 처리 : @ExceptionHandler
	// -> Dispatcher Servlet에서 <annotation-driven/>이 수행 되어야지만 가용 가능

	// 3) 전역(모든 클래스) 예외 처리 : @ControllerAdvice
	// -> 예외만을 처리하는 컨트롤러를 생성해서 운영.

	/*
	 * @ExceptionHandler(SQLException.class) public String DBException(Exception e,
	 * Model model) { e.printStackTrace();
	 * 
	 * model.addAttribute("errorMsg","DataBase 관련 오류 발생");
	 * 
	 * return "common/errorPage"; }
	 */

	@ExceptionHandler(Exception.class)
	public String etcException(Exception e, Model model) { // 특정 예외를 제외한 나머지 예외 처리

		e.printStackTrace(); // 예외 내용 출력

		model.addAttribute("errorMsg", "회원 관련 서비스 처리 중 오류 발생");

		return "common/errorPage";

	}

}
