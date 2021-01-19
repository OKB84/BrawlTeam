package com.example.demo.entity;

import lombok.Data;

/**
 * キャラごとの最多トロフィー中央値をマッピングする際に使用するクラス
 * プレイヤー詳細情報を表示するために必要な情報
 * @author root1
 *
 */
@Data
public class TrophyMedianEntity {

	private String playerTag;	// プレイヤータグ
	private String type;		// キャラクタータイプ
	private int offset;			// 各タイプのキャラクター数の半分の値
}
