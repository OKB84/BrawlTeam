package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.BrawlerStat;
import com.example.demo.controller.dto.PlayerInfoDto;
import com.example.demo.entity.OwnBrawlerEntity;
import com.example.demo.mapper.OwnBrawlerMapper;

/**
 * 所有キャラクターに関するサービスクラス
 * @author root1
 *
 */
@Service
public class OwnBrawlerService {

	@Autowired
	OwnBrawlerMapper mapper;

	// 新規作成（１件のみ）
	public int create(PlayerInfoDto dto) {

		// DTOからプレイヤータグと所有キャラクター情報を取得
		String playerTag = dto.getTag();
		List<BrawlerStat> brawlers = dto.getBrawlers();

		// INSERT内容格納用のEntityインスタンスを作成
		OwnBrawlerEntity entity = new OwnBrawlerEntity();
		entity.setPlayerTag(playerTag);

		int insertSuccessCount = 0;		// DB登録件数

		// 1件ずつ所有キャラクター情報を登録
		for(BrawlerStat b : brawlers) {
			entity.setBrawlerId(b.getId());
			entity.setRank(b.getRank());
			entity.setTrophies(b.getTrophies());
			entity.setHighestTrophies(b.getHighestTrophies());
			entity.setPower(b.getPower());
			insertSuccessCount += mapper.create(entity);
		}

		return insertSuccessCount;
	}

	// 新規作成（複数件一括）
	public int create(List<PlayerInfoDto> playerInfoList) {

		int insertSuccessCount = 0;		// DB登録件数

		for (PlayerInfoDto dto : playerInfoList) {
			insertSuccessCount += create(dto);
		}

		return insertSuccessCount;
	}

	// 所有キャラクター情報一覧の取得
	public List<String> search(String playerTag) {
		return mapper.search(playerTag);
	}
}
