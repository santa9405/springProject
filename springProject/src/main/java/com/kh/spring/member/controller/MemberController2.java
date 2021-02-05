package com.kh.spring.member.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService2;
import com.kh.spring.member.model.vo.Member;

@Controller
@RequestMapping("/member2/*")
@SessionAttributes({"loginMember"}) // 세션에 있는 "loginMember" 키값을 가진 데이터를
									// 해당 클래스내의 Model이 얻어가서 쓸 수 있게 함.
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
	
	// 마이페이지 전환용 Controller
	@RequestMapping("mypage")
	public String myPage() {
		return "member/mypage";
	}
	
	// 회원 정보 수정 Controller
	@RequestMapping(value = "updateAction", method = RequestMethod.POST)
	public String updateAction(@ModelAttribute Member updateMember,
								Model model, RedirectAttributes ra,
								@ModelAttribute(name = "loginMember", binding = false) Member loginMember) {
								// binding 속성 : 요청 파라미터를 해당 객체에 반영 할것인가?
		
		// updateMember : email, phone, address, interest
		
		// 세션에서 회원 번호를 얻어오는 방법
		// 1) HttpSession의 getAttribute("loginMember")
		// 2) Model, @SessionAttributes로 세션에 등록된 값은 얻어오는 것도 가능함
		//Member loginMember = (Member) model.getAttribute("loginMember");
		
		// 3) @ModelAttribute를 이용하여 Model로 세팅한 값을 반대로 얻어오는 것도 가능함
		// 매개변수에 @ModelAttribute("모델로 등록한 key값") 자료형 변수명
		//System.out.println(loginMember);
		
		System.out.println(updateMember);
		System.out.println(loginMember);
		
		// 로그인 정보에서 회원 번호를 얻어와 updateMember에 세팅
		updateMember.setMemberNo(loginMember.getMemberNo());
		
		// 수정된 회원정보 + 로그인된 회원의 번호를 가지고 Service 수행
		int result = service.updateAction(updateMember);
		
		// 성공 시 : success, 회원 정보 수정 성공
		if(result > 0) {
			swalIcon = "success";
			swalTitle = "회원 정보 수정 성공";
			
			// session에 저장된 변경 전 회원정보를 수정된 내용으로 변경
			loginMember.setMemberPhone(updateMember.getMemberPhone());
			loginMember.setMemberEmail(updateMember.getMemberEmail());
			loginMember.setMemberAddress(updateMember.getMemberAddress());
			loginMember.setMemberInterest(updateMember.getMemberInterest());
			
			model.addAttribute("loginMember",loginMember);
			
		}else {
			// 실패 시 : error, 회원 정보 수정 실패
			swalIcon = "error";
			swalTitle = "회원 정보 수정 실패";
		}
		
		ra.addFlashAttribute("swalIcon", swalIcon);
		ra.addFlashAttribute("swalTitle", swalTitle);
		
		// 성공/실패 관계없이 다시 마이페이지 재요청
		return "redirect:mypage";
	}
	
	// 비밀번호 변경 화면 전환용 Controller
	@RequestMapping("changePwd")
	public String changePwd() {
		return "member/changePwd";
	}
	
	// 비밀번호 변경 Controller
	@RequestMapping(value = "updatePwd", method = RequestMethod.POST)
	public String updatePwd(@RequestParam("memberPwd") String memberPwd,
							@RequestParam("newPwd1") String newPwd,
							@ModelAttribute(name = "loginMember", binding = false) Member loginMember,
							RedirectAttributes ra) {
		
		// Map을 이용하여 필요한 데이터를 하나로 묶어둠
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberPwd", memberPwd);
		map.put("newPwd", newPwd);
		map.put("memberNo", loginMember.getMemberNo());
		
		// 비밀번호 변경 Service 호출
		int result = service.updatePwd(map);
		
		// 결과에 따른 쟈요청 주소를 저장할 변수
		String returnUrl = null;
		
		// 비밀번호 변경 성공 시
		// success, 비밀번호 변경 성공, 마이페이지 재요청
		if(result > 0) {
			swalIcon = "success";
			swalTitle = "비밀번호 변경 성공";
			returnUrl = "mypage";
		}else {
			// 비밀번호 변경 실패 시
			// error, 비밀번호 변경 실패, 비밀번호 변경 페이지 재요청
			swalIcon = "error";
			swalTitle = "비밀번호 변경 실패";
			returnUrl = "changePwd";
			
		}
		
		ra.addFlashAttribute("swalIcon", swalIcon);
		ra.addFlashAttribute("swalTitle", swalTitle);
		
		return "redirect:" + returnUrl;
	}
	
	// 회원 탈퇴 화면 전환용 Controller
	@RequestMapping("secession")
	public String changeSecession() {
		return "member/secession";
	}
	
	// 회원 탈퇴 Controller
	@RequestMapping("updateStatus")
	public String changeSecession(@RequestParam("memberPwd") String memberPwd,
								  @RequestParam(value = "agree", required = true) String agree) {
		
		// Map을 이용하여 입력받은 비밀번호와 체크한 약관 동의를 하나로 묶어둠
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberPwd", memberPwd);
		map.put("agree", agree);
		
		//int result = service.updateStatus(map);
		
		return "member/secession";
	}
	
}
