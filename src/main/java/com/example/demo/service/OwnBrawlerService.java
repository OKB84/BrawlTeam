package com.example.demo.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.BrawlerStat;
import com.example.demo.controller.dto.OffsetForMedianDto;
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
	    List<OffsetForMedianDto> offsetList = mapper.getOffsetForMedian(playerTag);

	    // 各タイプの中央値を取得
	    for (OffsetForMedianDto offsetForMedianDto : offsetList) {

	    	entity.setType(offsetForMedianDto.getType());
	    	entity.setOffset(offsetForMedianDto.getOffsetNum());

	    	int median = mapper.getSingleMedian(entity);

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "1")) {
	    		dto.setMedianTroLongRange(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "2")) {
	    	    dto.setMedianTroLongRangeSupHeavy(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "3")) {
	    	    dto.setMedianTroMidRange(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "4")) {
	    	    dto.setMedianTroMidRangeSupHeavy(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "5")) {
	    	    dto.setMedianTroHeavyWeight(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "6")) {
	    	    dto.setMedianTroSemiHeavyWeight(median);
	    	}

	    	if (StringUtils.equals(offsetForMedianDto.getType(), "7")) {
	    	    dto.setMedianTroThrower(median);
	    	}
	    }

	    // 全タイプ合計の中央値を取得
	    entity.setOffset(Math.floorDiv(mapper.search(playerTag).size(), 2));
	    System.out.println(entity.getOffset());
	    dto.setMedianTroAllBrawlers(mapper.getEntireMedian(entity));

	    return dto;
	}
}
