package com.kh.spring.common.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// 전체 요청이 필터를 거치게 함
@WebFilter(urlPatterns = {"/*"})
public class LoginFilter implements Filter {

	// 로그인이 되어있지 않아도 접근 가능한(허용되는) 경로를 모아둘 Set 생성
	private static final Set<String> ALLOWED_PATH = new HashSet<String>();
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		// 로그인이 되어있지 않아도 접근 가능한 경로 추가
		//ALLOWED_PATH.add("/"); // 메인 페이지
		//ALLOWED_PATH.add("/resources/\\w"); // 이미지, css, js 파일 등을 접근할 수 있는 경로 추가
		
		// 회원 전용 페이지 중 로그인이 되어있지 않아도 접근 가능한 페이지 경로 추가
		ALLOWED_PATH.add("/member/login"); // 로그인 페이지 요청
		ALLOWED_PATH.add("/member/loginAction"); // 로그인 요청
		ALLOWED_PATH.add("/member2/signUp"); // 회원 가입 페이지 요청
		ALLOWED_PATH.add("/member2/signUpAction"); // 회원 가입 요청
		ALLOWED_PATH.add("/member2/idDupCheck"); // 아이디 중복검사 요청
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		HttpSession session = req.getSession();
		
		// 요청주소 확인
		String path = req.getRequestURI().substring( req.getContextPath().length() );
				//	  /spring/member/login					"/spring"이 삭제 되고 "/member/login"이 저장됨
		
		// 로그인 여부 확인
		boolean isLogin = session.getAttribute("loginMember") != null;
		
		// 요청 주소가 허용 목록에 있는 주소인지 확인
		boolean isAllowedPath = false;
		
		for(String p : ALLOWED_PATH) {
			
			// ALLOWED_PATH에 있는 주소와 요청주소가 일치할 경우
			if(Pattern.matches(p, path) ) {
				isAllowedPath = true;
				break;
			}
		}
		
		// 로그인이 되어있지 않은 상태로 요청 허용된 주소 == 로그인을 하지 않고 이용 가능한 요청 주소들
		if( !isLogin && isAllowedPath ) {
	         chain.doFilter(request, response);
	   
	      }else if(isLogin && !isAllowedPath ) {
	    	  // 로그인이 되어있을 경우 요청 허용되지 않은 주소 == 로그인을 했을 때만 이용 가능한 요청 주소들
	         chain.doFilter(request, response);
	         
	      } else { // 로그인이나 허용 주소 여부 관계없이
	    	  															// 아무 단어
	         if( Pattern.matches("/", path) 
	        		 || Pattern.matches("/resources/.*", path)
	        		 || Pattern.matches("/board/list/.*", path)) {
	        	 	 chain.doFilter(request, response);
	            
	         }else {
	        	// ex) !isLogin && path == /member2/mypage
	            res.sendRedirect(req.getContextPath()); // 메인페이지로 이동
	         }
	      }
		
	}

	public LoginFilter() {}
	public void destroy() {}
	public void init(FilterConfig fConfig) throws ServletException {}

}
