package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.PlayerClub;
import com.example.demo.entity.ClubEntity;
import com.example.demo.mapper.ClubMapper;

/**
 * クラブマスタに関するサービスクラス
 * @author root1
 *
 */
@Service
public class ClubService {

	@Autowired
	ClubMapper mapper;

	// クラブ情報の新規登録
	public int create(PlayerClub playerClub) {

		// クラブに所属していない場合は登録処理を行わない
		if (playerClub.getTag() == null) {
			return 0;
		}

		return mapper.create(setEntityFromDto(playerClub));
	}

	// DTOからEntityへの詰め替え処理を共通化したメソッド
	private ClubEntity setEntityFromDto(PlayerClub playerClub) {

		ClubEntity entity = new ClubEntity();
		entity.setTag(playerClub.getTag());
		entity.setName(playerClub.getName());
		return entity;
	}
}
