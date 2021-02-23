package com.kh.spring.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kh.spring.board.model.service.ReplyService;
import com.kh.spring.board.model.vo.Reply;

// RestController : Controller 내부에 작성된 메소드의 반환 값이 모두 값 자체인 Controller

//@Controller // 컨트롤러 + Bean 등록
@RestController // 값 자체만 반환하는 컨트롤러 + Bean 등록
@RequestMapping("/reply/*")
public class ReplyController {
	
	@Autowired
	private ReplyService service;
	
	// AJAX : 비동기로 통신하여 화면 전체 갱신이 아닌 일부를 갱신
	
	// selectReplyList/500
	// 댓글 목록 조회 Controller
	//@ResponseBody // 반환되는 값을 viewName이 아니라 값 자체로 인식 시키는 어노테이션
	@RequestMapping("selectReplyList/{parentBoardNo}")
	public String selectReplyList(@PathVariable("parentBoardNo") int parentBoardNo) {
		
		List<Reply> rList = service.selectReplyList(parentBoardNo);
		
		// JSON : 자바스크립트 객체 표기법 모양으로 작성된 문자열
		// GSON : Google에서 만든 JSON 라이브러리
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy년 MM월 dd일 HH:mm:ss").create();
		
		return gson.toJson(rList);
	}
	
	// 댓글 삽입 Controller
	@RequestMapping("insertReply/{parentBoardNo}")
	public int insertReply(@PathVariable("parentBoardNo") int parentBoardNo,
						   @RequestParam("replyWriter") int replyWriter,
						   String replyContent) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentBoardNo", parentBoardNo);
		map.put("replyWriter", replyWriter);
		map.put("replyContent", replyContent);
		
		int result = service.insertReply(map);
		
		return result;
	}
	
	// 댓글 수정 Controller
	@RequestMapping("updateReply/{replyNo}")
	public int updateReply(@PathVariable("replyNo") int replyNo,
						   @RequestParam("replyContent") String replyContent) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("replyNo", replyNo);
		map.put("replyContent", replyContent);
		
		int result = service.updateReply(map);
		
		return result;
	}

}
