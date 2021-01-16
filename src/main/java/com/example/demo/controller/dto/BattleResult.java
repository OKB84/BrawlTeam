package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * バトル結果に関する情報
 * @author root1
 *
 */
@Data
public class BattleResult {

	// 全モード共通プロパティ
	private String mode;				// バトルモード

	// 3on3、バトルロワイヤル共通
	private String type;				// 勝敗でトロフィーが増減するか否か
	private int trophyChange;			// 勝敗結果によるトロフィー増減値

	// 3on3
	private String result;				// 勝ち負け(victory, defeat, draw)
	private Player starPlayer;			// MVPプレイヤー
	private List<List<Player>> teams;	// 自分のチーム、対戦相手のチーム

	// バトルロワイヤル
	private int rank;					// 勝敗ランク（変数名は合っているはずなのになぜかAPIからデータを受け取れない）

	// バトルロワイヤル、ボスファイト等
	private List<Player> players;		// 参加プレイヤー
}
