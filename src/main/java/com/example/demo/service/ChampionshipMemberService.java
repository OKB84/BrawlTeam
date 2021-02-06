package com.example.demo.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ChampionshipMemberEntity;
import com.example.demo.mapper.ChampionshipMemberMapper;

/**
 * 大会参加候補メンバーに関するサービスクラス
 * @author root1
 *
 */
@Service
public class ChampionshipMemberService {

	@Autowired
	ChampionshipMemberMapper mapper;

	@Autowired
	HttpSession session;

	// 新規登録（一括登録）
	public int create(List<String> memberPlayerTagList) {

		int createCount = 0;	// INSERT成功回数

		for (String playerTag : memberPlayerTagList) {
			createCount += create(playerTag);
		}

		return createCount;
	}

	// 新規登録（１件のみ登録）
	public int create(String playerTag) {

		// 詰め替え用Entityを作成し、ログイン中のユーザIDをセット
		ChampionshipMemberEntity entity = new ChampionshipMemberEntity();
		entity.setUserId((Integer)session.getAttribute("userId"));
		entity.setMemberPlayerTag(playerTag);

		return mapper.create(entity);
	}

	// 検索
	public List<String> search() {
		// ログイン中のユーザIDでDB検索
		return mapper.searchByUserId((Integer)session.getAttribute("userId"));
	}

	// 削除
	public int delete(String playerTag) {

		// ログイン中のユーザIDをセットしたEntityに詰め替え
		ChampionshipMemberEntity entity = new ChampionshipMemberEntity();
		entity.setUserId((Integer)session.getAttribute("userId"));

		entity.setMemberPlayerTag(playerTag);

		return mapper.delete(entity);
	}

	// すでに登録されているメンバーか調査
	public boolean isExistedMember(String playerTag) {

		// 詰め替え用Entityを作成し、ログイン中のユーザIDをセット
		ChampionshipMemberEntity entity = new ChampionshipMemberEntity();
		entity.setUserId((Integer)session.getAttribute("userId"));
		entity.setMemberPlayerTag(playerTag);

		return mapper.searchIfExisted(entity).size() > 0 ? true : false;
	}
}
