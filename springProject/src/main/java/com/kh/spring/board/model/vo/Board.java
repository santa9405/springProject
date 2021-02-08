package com.kh.spring.board.model.vo;

import java.sql.Timestamp;

public class Board {

	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String memberId;
	private int readCount;
	private String categoryNm;
	private Timestamp boardCreateDt;
	private Timestamp boardModifyDt;
	private String boardStatus;
	private int boardCd;
	private String boardNm;
	
	public Board() { }

	public Board(int boardNo, String boardTitle, String boardContent, String memberId, int readCount, String categoryNm,
			Timestamp boardCreateDt, Timestamp boardModifyDt, String boardStatus, int boardCd, String boardNm) {
		super();
		this.boardNo = boardNo;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.memberId = memberId;
		this.readCount = readCount;
		this.categoryNm = categoryNm;
		this.boardCreateDt = boardCreateDt;
		this.boardModifyDt = boardModifyDt;
		this.boardStatus = boardStatus;
		this.boardCd = boardCd;
		this.boardNm = boardNm;
	}

	public int getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(int boardNo) {
		this.boardNo = boardNo;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardContent() {
		return boardContent;
	}

	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public String getCategoryNm() {
		return categoryNm;
	}

	public void setCategoryNm(String categoryNm) {
		this.categoryNm = categoryNm;
	}

	public Timestamp getBoardCreateDt() {
		return boardCreateDt;
	}

	public void setBoardCreateDt(Timestamp boardCreateDt) {
		this.boardCreateDt = boardCreateDt;
	}

	public Timestamp getBoardModifyDt() {
		return boardModifyDt;
	}

	public void setBoardModifyDt(Timestamp boardModifyDt) {
		this.boardModifyDt = boardModifyDt;
	}

	public String getBoardStatus() {
		return boardStatus;
	}

	public void setBoardStatus(String boardStatus) {
		this.boardStatus = boardStatus;
	}

	public int getBoardCd() {
		return boardCd;
	}

	public void setBoardCd(int boardCd) {
		this.boardCd = boardCd;
	}

	public String getBoardNm() {
		return boardNm;
	}

	public void setBoardNm(String boardNm) {
		this.boardNm = boardNm;
	}

	@Override
	public String toString() {
		return "Board [boardNo=" + boardNo + ", boardTitle=" + boardTitle + ", boardContent=" + boardContent
				+ ", memberId=" + memberId + ", readCount=" + readCount + ", categoryNm=" + categoryNm
				+ ", boardCreateDt=" + boardCreateDt + ", boardModifyDt=" + boardModifyDt + ", boardStatus="
				+ boardStatus + ", boardCd=" + boardCd + ", boardNm=" + boardNm + "]";
	}
	
	
	
}
