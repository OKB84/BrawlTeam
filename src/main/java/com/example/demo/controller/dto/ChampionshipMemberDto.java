package com.example.demo.controller.dto;

import lombok.Data;

/**
 * チャンピオンシップ新規作成時に使用されるクラス
 * プレイヤー詳細についてDBから取得してJOINされた、選択されたチーム番号も持つオブジェクト
 * @author root1
 *
 */
@Data
public class ChampionshipMemberDto {

	private String name;			// プレイヤー名
	private String playerTag;		// プレイヤータグ
	private String teamNum;			// チーム番号
	private int trophies;			// 各キャラクターの合計トロフィー数
	private int numOfBrawler;		// 所有キャラクター数
	private int highestTrophies;			// 最多トロフィー数
	private int powerPlayPoints;			// パワープレイポイント
	private int highestPowerPlayPoints;		// 最多パワープレイポイント
	private int soloVictories;				// ソロバトルロイヤル勝利数
	private int duoVictories;				// デュオバトルロイヤル勝利数
	private int avgTroAllBrawlers;			// 平均最多トロフィー数（全キャラクター）
	private int avgTroLongRange;			// 平均最多トロフィー数（長距離）
	private int avgTroLongRangeSupHeavy;	// 平均最多トロフィー数（長距離タンクメタ）
	private int avgTroMidRange;				// 平均最多トロフィー数（中距離）
	private int avgTroMidRangeSupHeavy;		// 平均最多トロフィー数（中距離タンクメタ）
	private int avgTroHeavyWeight;			// 平均最多トロフィー数（タンク）
	private int avgTroSemiHeavyWeight;		// 平均最多トロフィー数（セミタンク）
	private int avgTroThrower;				// 平均最多トロフィー数（スローワー）
}
