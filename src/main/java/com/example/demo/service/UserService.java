package com.example.demo.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.PlayerInfoDto;
import com.example.demo.controller.dto.SocialMediaDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;

/**
 * システムユーザに関するサービスクラス
 * @author root1
 *
 */
@Service
public class UserService {

	@Autowired
	UserMapper mapper;
	@Autowired
	HttpSession session;
	@Autowired
	BrawlStarsAPIService brawlStarsAPIService;


	// ユーザ新規登録
	public int create(SocialMediaDto dto) {
		// プレイヤータグはnullで登録
		return mapper.create(dto);
	}

	// プレイヤータグを更新
	public int updatePlayerTag(String playerTag) {

		// Entityに詰め替え
		UserEntity entity = new UserEntity();
		entity.setId((Integer)session.getAttribute("userId"));
		entity.setPlayerTag(playerTag);

		return mapper.updatePlayerTag(entity);
	}

	// ソーシャルメディア情報を元に該当ユーザを検索
	public UserEntity search(SocialMediaDto dto) {

		List<UserEntity> list = mapper.searchBySocialMediaInfo(dto);

		// 検索結果が0件の場合はnullを返却
		return list.size() == 1 ? list.get(0) : null;
	}

	// ユーザ登録時に公式APIにアクセスしてプレイヤー情報を取得
	public PlayerInfoDto getPlayerInfo(String playerTag) {

		// プレイヤータグの先頭の#は%で送信しないとエラーになる
		playerTag = "%" + playerTag;
		return brawlStarsAPIService.getPlayerInfo(playerTag);
	}

	// セッションのユーザIDからログイン中のユーザ情報を検索
	public UserEntity search(int userId) {

		List<UserEntity> list = mapper.searchByUserId(userId);

		// 検索結果が0件の場合はnullを返却
		return list.size() == 1 ? list.get(0) : null;
	}

	// ユーザ削除
	public int delete(int userId) {
		return mapper.delete(userId);
	}
}
