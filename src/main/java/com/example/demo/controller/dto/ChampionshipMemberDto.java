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
}
