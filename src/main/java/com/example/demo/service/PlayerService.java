package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.BattleLog;
import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.controller.dto.Player;
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
	BrawlerMasterService brawlerMasterService;
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

		// バトルログ以外の情報を取得
		PlayerDetailDto playerDetailDto = mapper.getPlayerDetail(playerTag);
		playerDetailDto.setPlayerTag(playerTag);

		// 公式APIからバトルログ取得
		List<BattleLog> battleLogList = brawlStarsService.getBattleLog(playerTag).getItems();

		// 3on3の勝率を計算してセット
		playerDetailDto.setVictoryRate(setVictoryRate(battleLogList));

		// バトルログから引数で受け取ったプレイヤータグのプレイヤーの使用キャラクターを取得
		for (BattleLog battleLog : battleLogList) {

			List<List<Player>> teams = battleLog.getBattle().getTeams();
			List<Player> players = battleLog.getBattle().getPlayers();

			if (teams != null && teams.get(0).size() > 0) {
				// 3on3のモードの場合（例外が発生しないようnullチェック）
				for (List<Player> playerList : teams) {
					playerDetailDto = setUseBrawler(playerDetailDto, playerList);
				}
			}

			if (players != null && players.size() > 0) {
				// バトルロイヤルやボスファイト等の場合（例外が発生しないようnullチェック）
				playerDetailDto =  setUseBrawler(playerDetailDto, players);
			}
		}

		return playerDetailDto;
	}

	// バトルログ内のプレイヤーリストからキャラクタータイプごとの使用回数を算出してDTOに追加
	private PlayerDetailDto setUseBrawler(PlayerDetailDto dto, List<Player> list) {
		for (Player player : list) {
			// プレイヤータグが目的のプレイヤーであればキャラクタータイプをチェック
			if (StringUtils.equals(player.getTag().replace("#", "%"), dto.getPlayerTag())) {
				String type = brawlerMasterService.getType(player.getBrawler().getId());

				switch (type) {
					case "1":
						dto.setUseLongRange(dto.getUseLongRange() + 1);
						break;
					case "2":
						dto.setUseLongRangeSupHeavy(dto.getUseLongRangeSupHeavy() + 1);;
						break;
					case "3":
						dto.setUseMidRange(dto.getUseMidRange() + 1);
						break;
					case "4":
						dto.setUseMidRangeSupHeavy(dto.getUseMidRangeSupHeavy() + 1);
						break;
					case "5":
						dto.setUseHeavyWeight(dto.getUseHeavyWeight() + 1);
						break;
					case "6":
						dto.setUseSemiHeavyWeight(dto.getUseSemiHeavyWeight() + 1);
						break;
					case "7":
						dto.setUseThrower(dto.getUseThrower() + 1);
						break;
				}
			}
		}
		return dto;
	}

	// バトルログから3on3の勝率を算出
	private int setVictoryRate(List<BattleLog> battleLogList) {

		int battleCounter = 0;		// 3on3のバトル回数
		int victoryCounter = 0;		// 3on3の勝利回数

		// 3on3のモードの名前を配列化
		String[] modeArr = {
							"gemGrab",
							"brawlBall",
							"siege",
							"heist",
							"bounty",
							"hotZone",
							"presentPlunder"
						};

		// containsメソッドで処理の高速化を図るためリスト型に変換
		List<String> modeList = Arrays.asList(modeArr);

		for (BattleLog battleLog : battleLogList) {
			if (modeList.contains(battleLog.getEvent().getMode())) {
				// モードが3on3であればバトル回数をインクリメント
				battleCounter++;
				if (StringUtils.equals(battleLog.getBattle().getResult(), "victory")) {
					// 勝利していれば勝利回数をインクリメント
					victoryCounter++;
				}
			}
		}

		// 四捨五入して勝率（パーセント）を返却
		return Math.round(victoryCounter * 100 / battleCounter);
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
