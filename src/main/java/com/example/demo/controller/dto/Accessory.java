package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのプレイヤー情報返却時に使用されるクラス
 * 所有キャラクターのガジェット情報
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class Accessory {

	int id;			// integer, optional
	String name;	// JsonLocalizedName, optional
}
