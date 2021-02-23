<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<style>
/*댓글*/

.replyWrite>table {
	width: 90%;
	margin-top : 100px;
}

#replyContentArea {
	width: 90%;
}

#replyContentArea>textarea {
	resize: none;
	width: 100%;
}

#replyBtnArea {
	width: 100px;
	text-align: center;
}

.rWriter {
	display : inline-block;
	margin-right: 30px;
	vertical-align: top;
	font-weight: bold;
	font-size: 1.2em;
}

.rDate {
	display : inline-block;
	font-size: 0.5em;
	color: gray;
}

#replyListArea {
	list-style-type: none;
}

.rContent, .btnArea{
	display: inline-block;
	box-sizing: border-box;
}

.rContent {
	height: 100%;
	width : 100%;
	word-break:normal;
}


.replyBtnArea {
	text-align: right;
}


.replyUpdateContent {
	resize: none;
	width: 100%;
}


.reply-row{
	border-top : 1px solid #ccc;
	padding : 15px 0;
}

/* 답글 */
.childReply-li{
	padding-left: 50px;  
}

.childReplyArea{
	padding-top : 30px;
	width : 100%;
  text-align: right;
}

.childReplyContent{
	resize: none;  
	width : 100%; 
}


</style>
<div id="reply-area ">
	<!-- 댓글 작성 부분 -->
	<div class="replyWrite">
		<table align="center">
			<tr>
				<td id="replyContentArea"><textArea rows="3" id="replyContent"></textArea>
				</td>
				<td id="replyBtnArea">
					<button class="btn btn-success" id="addReply">댓글<br>등록</button>
				</td>
			</tr>
		</table>
	</div>


	<!-- 댓글 출력 부분 -->
	<div class="replyList mt-5 pt-2">
		<ul id="replyListArea">
			
		
		</ul>
	</div>

	
</div>

<script>
var loginMemberId = "${loginMember.memberId}"; // 로그인한 회원 아이디(있으면 아이디, 없으면 빈문자열)
var replyWriter = "${loginMember.memberNo}"; // 로그인한 회원 번호("1" == 1 == true)
var parentBoardNo = ${board.boardNo}; // 게시글 번호

// 댓글
// 페이지 로딩 완료 시 댓글 목록 호출
$(function(){
	selectReplyList();
});

// 댓글 목록 불러오기(AJAX)
function selectReplyList(){
	
	$.ajax({
		url : "${contextPath}/reply/selectReplyList/" + parentBoardNo,
		type : "post",
		dataType : "json",
		success : function(rList){
			
			// 조회된 댓글을  화면에 추가(새로운 요소(태그) 생성)
			// rList에는 현재 게시글의 댓글 List가 담겨 있음.
	         
      var replyListArea = $("#replyListArea");
      
      replyListArea.html(""); // 기존 정보 초기화
      
      // 댓글 List 반복 접근
      $.each(rList, function(index, item){   
         
         // 댓글을 출력할 li 요소를 생성
         var li = $("<li>").addClass("reply-row");
         
         
         // 댓글의 깊이가 1인 요소는 대댓글 이므로 별도 스타일을 적용할 수 있도록 childReply-li 클래스를 추가
         if(item.replyDepth == 1){
            li.addClass("childReply-li");
         }
         
         // 작성자, 작성일, 수정일 영역 
         var div = $("<div>");
         var rWriter = $("<a>").addClass("rWriter").html(item.memberId);
         var rDate = $("<p>").addClass("rDate").html("작성일 : " + item.replyCreateDate + "<br>마지막 수정 날짜 : " + item.replyModifyDate);
         div.append(rWriter).append(rDate)
         
         
         // 댓글 내용
         var rContent = $("<p>").addClass("rContent").html(item.replyContent);
         
         // 대댓글, 수정, 삭제 버튼 영역
         var replyBtnArea = $("<div>").addClass("replyBtnArea");
         
         // 로그인 되어 있고, 대댓글이 아닐 경우 경우에 답글 버튼 추가
         if(loginMemberId != "" && item.replyDepth != 1){
            var childReply = $("<button>").addClass("btn btn-sm btn-success ml-1 childReply").text("답글").attr("onclick", "addChildReplyArea(this, "+ item.parentReplyNo + ")");
            replyBtnArea.append(childReply);
         }
         
         // 현재 댓글의 작성자와 로그인한 멤버의 아이디가 같을 때 버튼 추가
         if(item.memberId == loginMemberId){
            
            // ** 추가되는 댓글에 onclick 이벤트를 부여하여 버튼 클릭 시 수정, 삭제를 수행할 수 있는 함수를 이벤트 핸들러로 추가함. 
            var showUpdate = $("<button>").addClass("btn btn-success btn-sm ml-1").text("수정").attr("onclick", "showUpdateReply(" + item.replyNo + ", this)");
            var deleteReply = $("<button>").addClass("btn btn-success btn-sm ml-1").text("삭제").attr("onclick", "deleteReply(" + item.replyNo + ")");
            
            replyBtnArea.append(showUpdate, deleteReply);
         }
         
         
         // 댓글 하나로 합치기
         li.append(div).append(rContent).append(replyBtnArea);
         
         // 댓글 영역을 화면에 배치
         replyListArea.append(li);
      });
	         
		}, error : function(){
			console.log("댓글 목록 조회 실패");			
		}
		
	});
	
}

//-----------------------------------------------------------------------------------------

// 댓글 등록
$("#addReply").on("click", function(){
	
	// 로그인이 되어있는지 확인
	if(loginMemberId == ""){ // 로그인이 되어있지 않은 경우
		swal({icon : "info", title : "로그인 후 이용해 주세요."});
	}else{ // 로그인이 된 경우
		
		var replyContent = $("#replyContent").val(); // 작성된 댓글 내용을 얻어와 저장
		
		if(replyContent.trim().length == 0){ // 댓글이 작성되지 않은 경우
			swal({icon : "info", title : "댓글 작성 후 클릭해주세요."})
		}else{ // 로그인O, 댓글 작성O 인 경우
			
			$.ajax({
				url : "${contextPath}/reply/insertReply/" + parentBoardNo,
				type : "post",
				data : {"replyWriter" : replyWriter, "replyContent" : replyContent},
				success : function(result){
					
					if(result > 0){ // 댓글 삽입 성공
						$("#replyContent").val(""); // 작성한 댓글 내용을 삭제
						swal({icon : "success", title : "댓글 삽입 성공"});
						selectReplyList(); // 다시 목록 조회
					}
					
				}, error : function(){
					console.log("댓글 삽입 실패");
				}
				
			});
			
		}
		
	}
	
});

//-----------------------------------------------------------------------------------------

//댓글 수정 폼

var beforeReplyRow;

function showUpdateReply(replyNo, el){
	// replyNo : 수정하려는 댓글 번호
	// el : 클릭된 "수정"버튼 요소 자체
	
  // 이미 열려있는 댓글 수정 창이 있을 경우 닫아주기
  if($(".replyUpdateContent").length > 0){
     $(".replyUpdateContent").eq(0).parent().html(beforeReplyRow);
  }
  
  // 댓글 수정화면 출력 전 요소를 저장해둠.
  beforeReplyRow = $(el).parent().parent().html();
  
  // 작성되어있던 내용(수정 전 댓글 내용) 
  var beforeContent = $(el).parent().prev().html();
  
  
  // 이전 댓글 내용의 크로스사이트 스크립트 처리 해제, 개행문자 변경
  // -> 자바스크립트에는 replaceAll() 메소드가 없으므로 정규 표현식을 이용하여 변경
  beforeContent = beforeContent.replace(/&amp;/g, "&");   
  beforeContent = beforeContent.replace(/&lt;/g, "<");   
  beforeContent = beforeContent.replace(/&gt;/g, ">");   
  beforeContent = beforeContent.replace(/&quot;/g, "\"");   
  
  beforeContent = beforeContent.replace(/<br>/g, "\n");   
  
  
  // 기존 댓글 영역을 삭제하고 textarea를 추가 
  $(el).parent().prev().remove();
  var textarea = $("<textarea>").addClass("replyUpdateContent").attr("rows", "3").val(beforeContent);
  $(el).parent().before(textarea);
  
  
  // 수정 버튼
  var updateReply = $("<button>").addClass("btn btn-success btn-sm ml-1 mb-4").text("댓글 수정").attr("onclick", "updateReply(" + replyNo + ", this)");
  
  // 취소 버튼
  var cancelBtn = $("<button>").addClass("btn btn-success btn-sm ml-1 mb-4").text("취소").attr("onclick", "updateCancel(this)");
  
  var replyBtnArea = $(el).parent();
  
  $(replyBtnArea).empty(); 
  $(replyBtnArea).append(updateReply); 
  $(replyBtnArea).append(cancelBtn); 
	
}

//-----------------------------------------------------------------------------------------


//댓글 수정
function updateReply(replyNo, el){
	// replyNo : 수정하려는 댓글 번호
	// el : "댓글 수정" 버튼 자체
	
	// 수정된 댓글 내용 저장
	var replyContent = $(el).parent().prev().val();
	
	if(replyContent.trim().length == 0){ // 댓글이 작성되지 않은 경우
		swal({icon : "info", title : "댓글을 입력해주세요."});
	}else{
		$.ajax({
			url : "${contextPath}/reply/updateReply/" + replyNo,
			type : "post",
			data : {"replyContent" : replyContent},
			success : function(result){
				
				if(result > 0){
					swal({icon : "success", title : "댓글 수정 성공"});
					selectReplyList();
				}
				
			}, error : function(){
				console.log("댓글 수정 실패");
			}
			
		});
	}
	
}

//-----------------------------------------------------------------------------------------


//댓글 수정 취소 시 원래대로 돌아가기
function updateCancel(el){
	// el : "취소" 버튼 자체
	// beforeReplyRow : 수정 전 댓글 내용이 있던 요소
	$(el).parent().parent().html(beforeReplyRow);
}
//-----------------------------------------------------------------------------------------

//댓글 삭제
function deleteReply(replyNo){
	
}

</script>