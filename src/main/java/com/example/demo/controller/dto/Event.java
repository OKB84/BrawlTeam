package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * バトルモードやマップに関する情報
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class Event {

	private int id;			// integer, optional
	private String mode;	// string, optional
	private String map;		// JsonLocalizedName, optional
}
