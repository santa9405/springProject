package com.kh.spring.member.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService2;
import com.kh.spring.member.model.vo.Member;

@Controller
@RequestMapping("/member2/*")
public class MemberController2 {

	@Autowired // 해당 자료형과 일치하는 bean을 의존성 주입(DI)
	private MemberService2 service;
	
	// sweet alert 메세지 전달용 변수 선언
	private String swalIcon;
	private String swalTitle;
	private String swalText;
	
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
	
	// 회원 가입 Controller
	@RequestMapping("signUpAction")
	public String signUpAction(@ModelAttribute Member signUpMember,
								RedirectAttributes ra // redirect시 데이터 전달용 객체
								/*, String memberInterest*/) {
		
		// signUpMember : 회원 가입 시 입력한 값들이 저장된 커맨드 객체
		//System.out.println(signUpMember);
		
		// 동일한 name 속성을 가진 input태그 값은
		// String[]에 저장할 경우 배열 요소로 저장되며
		// String으로 저장할 경우 ","로 구분된 한 줄의 문자열이 된다.
		//System.out.println(Arrays.toString(memberInterest));
		//System.out.println(memberInterest);
		
		// 회원 가입 서비스 호출(성공 시 1, 실패시 0 이 반환됨 (mybatis-insert))
		int result = service.signUp(signUpMember);
		
		// 회원 가입 성공 여부에 따른 메세지 지정
		if(result > 0) { // 성공
			swalIcon = "success";
			swalTitle = "회원 가입 성공";
			swalText = "회원 가입을 환영합니다.";
		}else { // 실패
			swalIcon = "success";
			swalTitle = "회원 가입 실패";
			swalText = "회원 가입 과정에서 문제가 발생했습니다.";
		}
		
		ra.addFlashAttribute("swalIcon", swalIcon);
		ra.addFlashAttribute("swalTitle", swalTitle);
		ra.addFlashAttribute("swalText", swalText);
		
		return "redirect:/"; // 가입 여부 상관없이 메인화면 재요청
		
	}
	
	// 내정보 페이지 전환용 Controller
	@RequestMapping("mypage")
	public String myPage() {
		return "member/mypage";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
