package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * 直近25試合以下のモードや勝敗、参加プレイヤーに関する情報
 * @author root1
 *
 */
@Data
public class BattleLog {

	private Event event;			// Event, optional
	private BattleResult battle;	// BattleResult, optional
	private String battleTime;		// string, optional
}
