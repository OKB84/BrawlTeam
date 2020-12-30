package com.example.demo.entity;
import java.util.Date;

import lombok.Data;

/**
 * own_brawlerテーブルのレコードをマッピングする際に使用するクラス
 * プレイヤーとキャラクターマスタを紐付けた所有キャラクター情報
 * @author root1
 *
 */
@Data
public class OwnBrawlerEntity {

	private String playerTag;		// プレイヤータグ
	private int brawlerId;			// キャラクターID
	private int rank;				// ランク
	private int trophies;			// トロフィー数
	private int highestTrophies;	// 最多トロフィー数
	private int power;				// パワーレベル
	private Date createdAt;			// レコード作成日時
	private Date updatedAt;			// レコード更新日時
}
