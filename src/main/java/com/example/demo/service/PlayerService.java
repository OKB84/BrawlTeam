package com.example.demo.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.controller.dto.PlayerDetailDto;
import com.example.demo.controller.dto.PlayerInfoDto;
import com.example.demo.entity.PlayerEntity;
import com.example.demo.mapper.PlayerMapper;

/**
 * プレイヤー基本情報に関するサービスクラス
 * @author root1
 *
 */
@Service
public class PlayerService {

	@Autowired
	HttpSession session;
	@Autowired
	PlayerMapper mapper;
	@Autowired
	BrawlStarsAPIService brawlStarsService;

	// プレイヤーマスタにデータを追加（１件のみ登録）
	// すでに登録済みのプレイヤータグの場合は、名前、トロフィー、クラブタグをアップデート
	public int createOnDuplicateKeyUpdate(PlayerInfoDto dto) {

		return mapper.createOnDuplicateKeyUpdate(setEntityFromDto(dto));
	}

	// プレイヤーマスタにデータを追加（一括登録）
	// すでに登録済みのプレイヤータグの場合は、名前、トロフィー、クラブタグをアップデート
	public int createOnDuplicateKeyUpdate(List<PlayerInfoDto> list) {

		int successCount = 0;	// INSERT成功回数

		for (PlayerInfoDto dto : list) {
			successCount += mapper.createOnDuplicateKeyUpdate(setEntityFromDto(dto));
		}

		return successCount;
	}

	// ログイン中のユーザの大会参加候補メンバーについてプレイヤー情報を取得
	public List<ChampionshipMemberDto> getMembersPlayerInfo() {

		return mapper.getMembersPlayerInfo((Integer)session.getAttribute("userId"));
	}

	// プレイヤー情報を更新
	public int update(List<PlayerInfoDto> playerInfoDtoList) {

		int successCount = 0;		// UPDATE件数確認用

		for (PlayerInfoDto dto : playerInfoDtoList) {
			// 更新前と情報が変わらない場合は更新件数にカウントされない？
			successCount += mapper.update(setEntityFromDto(dto));
		}

		return successCount;
	}

	// プレイヤータグに該当するプレイヤーを検索
	public PlayerEntity search(String playerTag) {

		return mapper.searchByPlayerTag(playerTag);
	}

	// プレイヤーの詳細情報を取得
	public PlayerDetailDto getPlayerDetail(String playerTag) {

		return mapper.getPlayerDetail(playerTag);
	}


	// DTOからEntityへのデータ詰め替えを共通化したメソッド
	private PlayerEntity setEntityFromDto(PlayerInfoDto dto) {

		PlayerEntity entity = new PlayerEntity();
		entity.setPlayerTag(dto.getTag());
		entity.setName(dto.getName());
		entity.setTrophies(dto.getTrophies());
		entity.setExpLevel(dto.getExpLevel());
		entity.setExpPoints(dto.getExpPoints());
		entity.setHighestTrophies(dto.getHighestTrophies());
		entity.setPowerPlayPoints(dto.getPowerPlayPoints());
		entity.setHighestPowerPlayPoints(dto.getHighestPowerPlayPoints());
		entity.setSoloVictories(dto.getSoloVictories());
		entity.setDuoVictories(dto.getDuoVictories());
		entity.setBestRoboRumbleTime(dto.getBestRoboRumbleTime());
		entity.setBestTimeAsBigBrawler(dto.getBestTimeAsBigBrawler());
		entity.setClubTag(dto.getClub().getTag());
		return entity;
	}

	// ログイン中のユーザが所属するクラブ内のプレイヤーのタグを取得
//	public List<String> getCurrentClubPlayerTag(String clubTag) {
//		return mapper.getCurrentClubPlayerTag(clubTag);
//	}
}
