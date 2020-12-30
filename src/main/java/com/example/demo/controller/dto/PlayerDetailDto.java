package com.example.demo.controller.dto;

import lombok.Data;

/**
 * プレイヤー詳細情報表示の際に使用されるクラス
 * @author root1
 *
 */
@Data
public class PlayerDetailDto {

	private String name;					// プレイヤー名
	private int trophies;					// トロフィー数
	private int highestTrophies;			// 最多トロフィー数
	private int powerPlayPoints;			// パワープレイポイント
	private int highestPowerPlayPoints;		// 最多パワープレイポイント
	private int soloVictories;				// ソロバトルロイヤル勝利数
	private int duoVictories;				// デュオバトルロイヤル勝利数
	private int numOfBrawler;				// キャラクター数
	private int avgTroAllBrawlers;			// 平均最多トロフィー数（全キャラクター）
	private int avgTroLongRange;			// 平均最多トロフィー数（長距離）
	private int avgTroLongRangeSupHeavy;	// 平均最多トロフィー数（長距離タンクメタ）
	private int avgTroMidRange;				// 平均最多トロフィー数（中距離）
	private int avgTroMidRangeSupHeavy;		// 平均最多トロフィー数（中距離タンクメタ）
	private int avgTroHeavyWeight;			// 平均最多トロフィー数（タンク）
	private int avgTroSemiHeavyWeight;		// 平均最多トロフィー数（セミタンク）
	private int avgTroThrower;				// 平均最多トロフィー数（スローワー）
}
