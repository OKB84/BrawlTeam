package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.BrawlerStat;
import com.example.demo.controller.dto.PlayerDetailDto;
import com.example.demo.controller.dto.PlayerInfoDto;
import com.example.demo.entity.OwnBrawlerEntity;
import com.example.demo.entity.TrophyMedianEntity;
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

	// トロフィー数の中央値を取得
	public PlayerDetailDto setMedian(PlayerDetailDto dto) {

		String playerTag = dto.getPlayerTag();

		// データ取得用のエンティティに詰め替えてプレイヤータグをセット
	    TrophyMedianEntity entity = new TrophyMedianEntity();
	    entity.setPlayerTag(playerTag);

	    // 各タイプの所有キャラクター数の半分の値を取得
	    List<Integer> offsetList = mapper.getOffsetForMedian(playerTag);

	    // 各タイプの中央値を取得
    	entity.setType("1");
    	entity.setOffset(offsetList.get(0));
	    dto.setMedianTroLongRange(mapper.getSingleMedian(entity));
    	entity.setType("2");
    	entity.setOffset(offsetList.get(1));
	    dto.setMedianTroLongRangeSupHeavy(mapper.getSingleMedian(entity));
    	entity.setType("3");
    	entity.setOffset(offsetList.get(2));
	    dto.setMedianTroMidRange(mapper.getSingleMedian(entity));
    	entity.setType("4");
    	entity.setOffset(offsetList.get(3));
	    dto.setMedianTroMidRangeSupHeavy(mapper.getSingleMedian(entity));
    	entity.setType("5");
    	entity.setOffset(offsetList.get(4));
	    dto.setMedianTroHeavyWeight(mapper.getSingleMedian(entity));
    	entity.setType("6");
    	entity.setOffset(offsetList.get(5));
	    dto.setMedianTroSemiHeavyWeight(mapper.getSingleMedian(entity));
    	entity.setType("7");
    	entity.setOffset(offsetList.get(6));
	    dto.setMedianTroThrower(mapper.getSingleMedian(entity));

	    // 全タイプ合計の中央値を取得
	    entity.setOffset(Math.floorDiv(mapper.search(playerTag).size(), 2));
	    System.out.println(entity.getOffset());
	    dto.setMedianTroAllBrawlers(mapper.getEntireMedian(entity));

	    return dto;
	}
}
