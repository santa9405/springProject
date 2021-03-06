package com.kh.spring.board.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.PageInfo;
import com.kh.spring.member.model.vo.Member;

@Controller // 컨트롤러임을 알려줌 + bean 등록
@SessionAttributes({"loginMember"})
@RequestMapping("/board/*")
public class BoardController {

	// Service 객체 의존성 주입
	@Autowired // 등록된 bean 중에서 같은 타입인 bean을 자동적으로 의존성 주입
	private BoardService service;
	
	private String swalIcon = null;
	private String swalTitle = null;
	private String swalText = null;
	
	
	// list/1
	
	// @PathVariable : @RequestMapping에 작성된 URL 경로에 있는 특정 값을
	//				       변수로 사용할 수 있게 하는 어노테이션
	
	// 게시글 목록 조회 Controller
	@RequestMapping("list/{type}")
	public String boardList( @PathVariable("type") int type,
							 @RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
							 Model model) {
		
		//System.out.println("type : " + type);
		//System.out.println("cp : " + cp);
		
		// 1) 페이징 처리를 위한 객체 PageInfo 생성
		PageInfo pInfo = service.getPageInfo(type, cp);
		
		//System.out.println(pInfo);
		
		// 2) 게시글 목록 조회
		List<Board> bList = service.selectList(pInfo);
		
		/*for(Board b : bList) {
			System.out.println(b);
		}*/
		
		if(bList != null && !bList.isEmpty()) { // 게시글 목록 조회성공 시
			List<Attachment> thumbnailList = service.selectThumbnailList(bList);
			
			if(thumbnailList != null) {
				model.addAttribute("thList", thumbnailList);
			}
		}
		
		// 게시글 목록, 페이징 처리 정보를 request scope로 세팅 후 sorward 진행
		model.addAttribute("bList", bList);
		model.addAttribute("pInfo", pInfo);
		
		return "board/boardList";
		
	}
	
	// 게시글 상세조회 Controller
	// 1/500
	@RequestMapping("{type}/{boardNo}")
	public String boardView(@PathVariable("type") int type,
							@PathVariable("boardNo") int boardNo,
							Model model, 
							@RequestHeader(value = "referer", required = false) String referer,
							RedirectAttributes ra) {
		
		// @RequestHeader(name="referer") String referer
		// --> HTTP 요청 헤더에 존재하는 "referer" 값을 얻어와
		// 	      매개변수 String referer에 저장
		
		System.out.println("type : " + type);
		System.out.println("boardNo : " + boardNo);
		
		// 게시글 상세조회 Service 호출
		Board board = service.selectBoard(boardNo, type);
		
		/*if(board.getBoardCd() != type) {
			board = null;
		}*/
		
		String url = null;
		
		if(board != null) { // 상세 조회 성공 시
			
			// 상세조회 성공한 게시물의 이미지 목록을 조회하는 Service 호출
			List<Attachment> attachmentList = service.selectAttachmentList(boardNo);
			
			// 조회된 이미지 목록이 있을 경우
			if(attachmentList != null && !attachmentList.isEmpty()) {
				model.addAttribute("attachmentList", attachmentList);
			}
			
			model.addAttribute("board", board);
			url = "board/boardView";
		}else {
			// 이전 요청 주소가 없는 경우 == 주소를 직접 작성할 경우 == 북마크를 통해 들어오는 경우
			if(referer == null) {
				url = "redirect:../list/" + type;
				
			}else { // 이전 요청 주소가 있는 경우
				url = "redirect:" + referer;
			}
			
			ra.addFlashAttribute("swalIcon", "error");
			ra.addFlashAttribute("swalTitle", "존재하지 않는 게시글입니다.");
			
		}
		
		return url;
	}
	
	// 게시글 등록 화면 전환용 Controller
	@RequestMapping("{type}/insert")
	public String insertView(@PathVariable("type") int type) {
		return "board/boardInsert" + type;
		// boardInsert1.jsp == 기존 방식
		// boardInsert2.jsp == summernote 적용 방식
	}
	
	// 게시글 등록(+ 파일 업로드) Controller
	@RequestMapping("{type}/insertAction")
	public String insertAction(@PathVariable("type") int type,
			   @ModelAttribute Board board,
			   @ModelAttribute("loginMember") Member loginMember,
			   @RequestParam(value="images", required = false) List<MultipartFile> images,
			   HttpServletRequest request,
			   RedirectAttributes ra) {
		
		// @RequestParam(value="images", required = false) List<MultipartFile> images
		//	-> <input type="file" name="images"> 태그를 모두 얻어와 images라는 List에 매핑
		
//		System.out.println("type : " + type);
//		System.out.println("board : " + board);
//		System.out.println("loginMember : " + loginMember);
		
		// map을 이용하여 필요한 데이터 모두 담기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberNo", loginMember.getMemberNo());
		map.put("boardTitle", board.getBoardTitle());
		map.put("boardContent", board.getBoardContent());
		map.put("categoryCode", board.getCategoryNm());
		map.put("boardType", type);
		
		// 파일 업로드 확인
		/*for(int i=0; i<images.size(); i++) {
			System.out.println("images[" + i + "] : " + images.get(i).getOriginalFilename());
		}*/
		
		// 파일 저장 경로 설정
		// HttpServletRequest 객체가 있어야지만 파일 저장 경로를 얻어올 수 있음.
		//	-> HttpServletRequest 객체는 Controller에서만 사용 가능
		
		// 파일이 업로드 되지 않은 부분도 출력되고 있음을 확인
		// == 모든 input type="file" 태그가 순서대로 넘어오고 있음을 확인
		// 	--> 넘어오는 순서를 fileLevel로 사용 가능
		String savePath = null;
		
		if(type == 1) {
			savePath = request.getSession().getServletContext().getRealPath("resources/uploadImages");
		}else {
			savePath = request.getSession().getServletContext().getRealPath("resources/infoImages");
		}
		
		//System.out.println(savePath);
		
		// 게시글 map, 이미지 images, 저장경로 savePath
		
		// 게시글 삽입 Service 호출
		int result = service.insertBoard(map, images, savePath);
		
		String url = null;
		
		// 게시글 삽입 결과에 따른 View 연결 처리
		if(result > 0){
			swalIcon = "success";
			swalTitle = "게시글 등록 성공";
			url = "redirect:" + result;
			
			// 새로 작성한 게시글 상세 조회 시 목록으로 버튼 경로 지정하기
			request.getSession().setAttribute("returnListURL", "../list/" + type);
			
		}else {
			swalIcon = "error";
			swalTitle = "";
			url = "redirect:insert";
		}
		
		ra.addFlashAttribute("swalIcon", swalIcon);
		ra.addFlashAttribute("swalTitle", swalTitle);
		
		return url;
		
	}
	
	/* @PathVariable, Query String 각각 언제 써야 되는가?
	 * 
	 * @PathVariable : 식별 용도로 사용
	 * 
	 * Query String : 주소 마지막에 k=v형태로 파라미터를 전달하는 문자열
	 * 		-> 필터링, 정렬 등 */
	
	// 게시글 수정 화면 전환용 Controller
	@RequestMapping("{type}/{boardNo}/update")
	public String update(@PathVariable("boardNo") int boardNo, 
						 @PathVariable("type") int type, Model model) {
		
		// 1) 게시글 상세 조회
		Board board = service.selectBoard(boardNo, type);
		
		// 2) 해당 게시글에 포함된 이미지 목록 조회
		if(board != null) {
			
			List<Attachment> attachmentList = service.selectAttachmentList(boardNo);
			model.addAttribute("attachmentList", attachmentList);
			// null 값이 전달되어도 EL이 빈 문자열로 처리해줌
		}
		
		model.addAttribute("board", board);
		
		return "board/boardUpdate";
	}
	
	// 게시글 수정 Controller
	@RequestMapping("{type}/{boardNo}/updateAction")
	public String updateAction(@PathVariable("boardNo") int boardNo,
							   @ModelAttribute Board updateBoard,
							   Model model, RedirectAttributes ra,
							   HttpServletRequest request,
			@RequestParam("deleteImages") boolean[] deleteImages,
			@RequestParam(value="images", required = false) List<MultipartFile> images) {
		
		/*System.out.println(Arrays.toString(deleteImages));
		for(MultipartFile m : images) {
			System.out.println(m.getOriginalFilename());
		}*/
		
		// boardNo를 updateBoard에 세팅
		updateBoard.setBoardNo(boardNo);
		
		// 파일 저장 경로 얻어오기
		String savePath = request.getSession().getServletContext().getRealPath("resources/uploadImages"); 
		
		// 파일 수정 Service 호출	   수정된 게시글 정보, 새롭게 업로드되거나 변경된 파일 정보 전체, 파일 저장 경로, 삭제된 파일레벨을 가지고 있는 배열
		int result = service.updateBoard(updateBoard, images, savePath, deleteImages);
		
		String url = null;
		
		if(result > 0) {
			swalIcon = "success";
			swalTitle = "게시글 수정 성공";
			url = "redirect:../" + boardNo;
		}else {
			swalIcon = "error";
			swalTitle = "게시글 수정 실패";
			url = "redirect:" + request.getHeader("referer");
		}
		
		ra.addFlashAttribute("swalIcon", swalIcon);
		ra.addFlashAttribute("swalTitle", swalTitle);
		
		return url;
	}
	
	// -------------------------------------------------summernote-----------------------------------------------
	// summernote에 업로드된 이미지 저장 Controller
	@ResponseBody // 응답 시 값 자체를 돌려보냄
	@RequestMapping("{type}/insertImage")
	public String insertImage(HttpServletRequest request, 
					@RequestParam(value = "uploadFile") MultipartFile uploadFile) {
		
		// 서버에 파일(이미지)을 저장할 폴더 경로 얻어오기
		String savePath
		 = request.getSession().getServletContext().getRealPath("resources/infoImages");
		
		Attachment at = service.insertImage(uploadFile, savePath);
		
		// java -> js로 객체 전달 : JSON
		return new Gson().toJson(at);
	}
	
}