package com.kh.spring.board.model.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDAO;
import com.kh.spring.board.model.exception.InsertAttachmentFailException;
import com.kh.spring.board.model.exception.UpdateAttachmentFailException;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.PageInfo;

@Service // 서비스임을 알려줌 + bean 등록
public class BoardServiceImpl implements BoardService{
	
	// DAO 객체 의존성 주입
	@Autowired
	private BoardDAO dao;

	// 게시글 목록  페이징처리 구현
	@Override
	public PageInfo getPageInfo(int type, int cp) {
		// 전체 게시글 수 조회
		int listCount = dao.getListCount(type);
		
		return new PageInfo(cp, listCount, type);
	}

	// 게시글 목록 조회 Service 구현
	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<Board> selectList(PageInfo pInfo) {
		return dao.selectList(pInfo);
	}

	// 썸네일 목록 조회 Service 구현
	@Override
	public List<Attachment> selectThumbnailList(List<Board> bList) {
		return dao.selectThumbnailList(bList);
	}

	// 게시글 상세 조회 Service 구현
	@Override
	public Board selectBoard(int boardNo, int type) {
		// 1) 게시글 상세 조회
		Board temp = new Board();
		temp.setBoardNo(boardNo);
		temp.setBoardCd(type);
		
		Board board = dao.selectBoard(temp);
		
		// 2) 상세 조회 성공 시 조회수 증가
		if( board != null ) {
			int result = dao.increaseReadCount(boardNo);
			
			if(result > 0) { //DB 조회 수 증가 성공 시
				// 먼저 조회된 board의 조회 수도 1 증가
				board.setReadCount(board.getReadCount() + 1);
				
			}
		}
		
		return board;
	} // selectBoard method end
	

	// 게시글에 포함된 이미지 목록 조회 Service 구현
	@Override
	public List<Attachment> selectAttachmentList(int boardNo) {
		return dao.selectAttachmentList(boardNo);
	}
	

	// 게시글 삽입 Service 구현
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertBoard(Map<String, Object> map, List<MultipartFile> images, String savePath) {
		int result = 0; // 최종 결과 저장 변수 선언
		
		// 1) 게시글 번호 얻어오기 -> SEQ_BNO.NEXTVAL를 통해 얻어옴
		int boardNo = dao.selectNextNo();
		
		// 2) 게시글 삽입
		if(boardNo > 0) { // 다음 게시글 번호를 얻어온 경우
			
			map.put("boardNo", boardNo); // map에 boardNo 추가
			
			// 크로스 사이트 스크립팅 방지 처리
			// 추후 summernote api 사용을 염두하여 
			// 게시판 타입별로 크로스 사이트 스크립팅 방지 처리를 선택적으로 진행
			if( (int)map.get("boardType") == 1) { // 자유게시판인 경우
				String boardTitle = (String)map.get("boardTitle");
				String boardContent = (String)map.get("boardContent");
				
				// 크로스 사이트 스크립팅 방지 처리 적용
				boardTitle = replaceParameter(boardTitle);
				boardContent = replaceParameter(boardContent);
				
				// 처리된 문자열을 다시 map에 세팅
				map.put("boardTitle", boardTitle);
				map.put("boardContent", boardContent);
				
				// 개행문자 처리 -> 화면에서 JSTL을 이용해서 처리할 예정
			}
			
			// 게시글 삽입 DAO 메소드 호출
			result = dao.insertBoard(map);
			
			// 3) 게시글 삽입 성공 시 이미지 정보 삽입
			if(result > 0) {
				
				// 이미지 정보를 Attachment객체에 저장 후 List에 추가
				List<Attachment> uploadImages = new ArrayList<Attachment>();
				
				// images.get(i).getOriginalFileName() -> 업로드된 파일의 원본 파일명
				//	--> 파일명 중복 상황을 대비하여 파일명을 변경하는 코드 필요 (rename() 메소드)
				
				// DB에 저장할 웹상 접근 주소(filePath)
				String filePath = "/resources/uploadImages";
				
				// for문을 이용하여 파일 정보가 담긴 images를 반복접근
				//	-> 업로드된 파일이 있을 경우에만 uploadImages 리스트에 추가
				for(int i=0; i<images.size(); i++) {
					// i == 인덱스 == fileLevel과 같은 값
					
					// 현재 접근한 images의 요소(MultipartFile)에 업로드된 파일이 있는지 확인
					if( !images.get(i).getOriginalFilename().equals("") ) {
						// 파일이 업로드 된 경우 == 업로드 된 원본 파일명이 있는 경우
						
						// 원본 파일명 변경
						String fileName = rename(images.get(i).getOriginalFilename());
						
						// Attachment 객체 생성
						Attachment at = new Attachment(filePath, fileName, i, boardNo);
						
						uploadImages.add(at); // 리스트에 추가
					}
				}
				
				// uploadImages 확인
				/*for(Attachment at : uploadImages) {
					System.out.println(at);
				}*/
				
				if(!uploadImages.isEmpty()) { // 업로드 된 이미지가 있을 경우
					// 파일 정보 삽입 DAO 호출
					result = dao.insertAttachmentList(uploadImages);
					// result == 삽입된 행의 개수
					
					// 모든 데이터가 정상 삽입 되었을 경우 --> 서버에 파일 저장
					if(result == uploadImages.size()) {
						result = boardNo; // result에 boardNo 저장
						
						// MultipartFile.transferTo
						//	-> MultipartFile 객체에 저장된 파일을
						//	       지정된 경로에 실제 파일의 형태로 변환하여 저장하는 메소드
						for(int i=0; i<uploadImages.size(); i++) {
							
							// uploadImages : 업로드된 이미지 정보를 담고있는 Attachment가 모여있는 List
							// images : input type="file" 태그의 정보를 담은 MultipartFile이 모여있는 List
						
							// uploadImages를 만들 때 각 요소의 파일 레벨은 images의 index를 이용하여 부여함.
						
							try {
								images.get(uploadImages.get(i).getFileLevel())
								.transferTo(new File(savePath + "/" + uploadImages.get(i).getFileName()));                
								
							}catch (Exception e) {
								e.printStackTrace();
								
								// transferTo()는 IOException을 발생 시킴(CheckedException)
								//	-> 어쩔 수 없이 try-catch 작성
								//	--> 예외가 처리되버려서 @Transactional이 정상동작하지 못함
								//	--> 꼭 예외처리를 하지 않아도 되는 UncheckedException을 강제 발생하여
								//		@Transactional이 예외가 발생했음을 감지하게 함
								//	-->  상황에 맞는 사용자 정의 예외를 작성
								throw new InsertAttachmentFailException("파일 서버 저장 실패");
							}
						}
							
					}else { // 파일 정보를 DB에 삽입하는데 실패했을 때
						throw new InsertAttachmentFailException("파일 정보 DB 삽입 실패");
					}
				}
			}
		}
		return result;
	}
	
	// 크로스 사이트 스크립트 방지 처리 메소드
	private String replaceParameter(String param) {
		String result = param;
		if(param != null) {
			result = result.replaceAll("&", "&amp;");
			result = result.replaceAll("<", "&lt;");
			result = result.replaceAll(">", "&gt;");
			result = result.replaceAll("\"", "&quot;");
		}
		
		return result;
	}
	
	// 파일명 변경 메소드
	public String rename(String originFileName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String date = sdf.format(new java.util.Date(System.currentTimeMillis()));
		
		int ranNum = (int)(Math.random()*100000); // 5자리 랜덤 숫자 생성
		
		String str = "_" + String.format("%05d", ranNum);
		//String.format : 문자열을 지정된 패턴의 형식으로 변경하는 메소드
		// %05d : 오른쪽 정렬된 십진 정수(d) 5자리(5)형태로 변경. 빈자리는 0으로 채움(0)
		
		String ext = originFileName.substring(originFileName.lastIndexOf("."));
		
		return date + str + ext;
	}

	// 게시글 수정 Service 구현
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateBoard(Board updateBoard, List<MultipartFile> images, String savePath, boolean[] deleteImages) {
		
		// 1) 게시글 수정
		// 제목, 내용 크로스사이트 스크립팅 방지 처리
		updateBoard.setBoardTitle( replaceParameter( updateBoard.getBoardTitle() ) );
		updateBoard.setBoardContent( replaceParameter( updateBoard.getBoardContent() ) );
		
		// 게시글 수정 DAO 호출
		int result = dao.updateBoard(updateBoard);
		
		// 2) 이미지 수정
		if( result > 0 ) {
			
			// 수정 전 업로드 되어있던 파일 정보를 얻어옴
			//	-> 새롭게 삽입 또는 수정되는 파일과 비교하기 위함
			List<Attachment> oldFiles = dao.selectAttachmentList(updateBoard.getBoardNo());
			
			// 새로 업로드된 파일 정보를 담을 리스트
			List<Attachment> uploadImages = new ArrayList<Attachment>();
			
			// 삭제 되어야 할 파일 정보를 담은 리스트
			List<Attachment> removeFileList = new ArrayList<Attachment>();
			
			// DB에 저장할 웹상 이미지 접근 경로
			String filePath = "/resources/uploadImages";
			
			// 새롭게 업로드된 파일 정보를 가지고 있는 images에 반복 접근
			for(int i=0; i<images.size(); i++) {
				
				// 업로드된 이미지가 있을 경우
				if( !images.get(i).getOriginalFilename().equals("") ) {
					
					// 파일명 변경
					String fileName = rename(images.get(i).getOriginalFilename());
					
					// Attachment 객체 생성
					Attachment at = new Attachment(filePath, fileName, i, updateBoard.getBoardNo());
					
					uploadImages.add(at); // 업로드 이미지 리스트에 추가
					
					// true : update 진행
					// false : insert 진행
					boolean flag = false;
					
					// 새로운 파일 정보와 이전 파일 정보를 비교하는 반복문
					for(Attachment old : oldFiles) {
						
						if(old.getFileLevel() == i ) {
							// 현재 접근한 이전 파일의 레벨이
							// 새롭게 업로드한 파일의 레벨과 같은 경우
							// == 같은 레벨에 새로운 이미지 업로드 --> update 진행
							flag = true;
							
							// DB에서 파일 번호가 일치하는 행의 내용을 수정하기 위해 파일번호를 얻어옴
							at.setFileNo( old.getFileNo() );
							
							removeFileList.add(old); // 삭제할 파일 목록에 이전 파일 정보 추가
						}
					}
					
					
					// flag 값에 따른 insert / update 제어
					if(flag) { // true : update 진행
						result = dao.updateAttachment(at);
						
					}else { // false : insert 진행
						result = dao.insertAttachment(at);
					}
					
					// insert 또는 update 실패 시 rollback 수행
					// -> 예외를 발생 시켜서 @Transactional을 이용해 수행
					if(result <= 0) {
						throw new UpdateAttachmentFailException("파일 정보 수정 실패");
					}
					
				} else { // 업로드된 이미지가 없을 경우
					
					
				}
			} // images 반복 접근 for문 종료
			
			// uploadImages == 업로드된 파일 정보 --> 서버에 파일 저장
			// removeFileList == 제거해야될 파일 정보 --> 서버에서 파일 삭제
			
			// 수정되거나 새롭게 삽입된 이미지를 서버에 저장하기 위해 transferTo() 수행
			if(result > 0) {
				for(int i=0 ; i<uploadImages.size(); i++) {
					
					try {
						images.get(uploadImages.get(i).getFileLevel())
							.transferTo(new File(savePath + "/" + uploadImages.get(i).getFileName()) );                                             
					}catch (Exception e) {
						e.printStackTrace();
						throw new UpdateAttachmentFailException("파일 정보 수정 실패");
					}
				}
			}
			
			// ------------------------------------------
			// 이전 파일 서버에서 삭제하는 코드 
			for(Attachment removeFile : removeFileList) {
				File tmp = new File(savePath + "/" + removeFile.getFileName());
				tmp.delete();
			}
			// ------------------------------------------
			
		}
		
		return result;
	}

}
