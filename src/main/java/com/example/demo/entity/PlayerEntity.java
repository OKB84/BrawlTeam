package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/**
 * playerテーブルのレコードをマッピングする際に使用するクラス
 * 所有キャラクター情報を除いたプレイヤー情報
 * @author root1
 *
 */
@Data
public class PlayerEntity {

	private String playerTag;				// プレイヤータグ
	private String name;					// プレイヤー名
	private int trophies;					// 全キャラクター合計トロフィー数
	private int expLevel;					// XPレベル
	private int expPoints;					// XPポイント
	private int highestTrophies;			// 最多トロフィー数
	private int powerPlayPoints;			// パワープレイポイント
	private int highestPowerPlayPoints;		// 最多パワープレイポイント
	private int soloVictories;				// ソロバトルロイヤル勝利数
	private int duoVictories;				// デュオバトルロイヤル勝利数
	private int bestRoboRumbleTime;			// ロボファイト最高レベル
	private int bestTimeAsBigBrawler;		// ビックゲーム巨大キャラ最高タイム
	private String clubTag;					// 所属クラブのタグ
	private Date createdAt;					// レコード作成日時
	private Date updatedAt;					// レコード更新日時
}
