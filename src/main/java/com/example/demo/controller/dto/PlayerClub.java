package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのプレイヤー情報返却時に使用されるクラス
 * プレイヤーの所属クラブ情報
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class PlayerClub {

	String tag;		// string, optional
	String name;	// string, optional
}
