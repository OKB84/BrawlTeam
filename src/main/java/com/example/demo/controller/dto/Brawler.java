package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * 使用キャラクターに関する情報
 * @author root1
 *
 */
@Data
public class Brawler {

	private int id;				// キャラクターID
	private String name;		// キャラクター名
	private int power;			// パワーレベル
	private int trophies;		// 現在トロフィー数
}
