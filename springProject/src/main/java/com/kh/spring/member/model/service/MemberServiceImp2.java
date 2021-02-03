package com.kh.spring.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.member.model.dao.MemberDAO2;

@Service
public class MemberServiceImp2 implements MemberService2 {

	@Autowired
	private MemberDAO2 dao;

	// 아이디 중복 체크 Service 구현
	@Override
	public int idDupCheck(String memberId) {
		return dao.idDupCheck(memberId);
	}
}
