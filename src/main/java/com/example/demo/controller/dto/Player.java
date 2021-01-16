package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * バトルのMVPプレイヤーに関する情報
 * @author root1
 *
 */
@Data
public class Player {

	private String tag;			// プレイヤータグ
	private String name;		// プレイヤー名
	private Brawler brawler;	// 使用キャラクター
}
