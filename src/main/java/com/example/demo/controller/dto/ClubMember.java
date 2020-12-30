package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 公式APIからのクラブ所属メンバー情報返却時で使用されるクラス
 * クラブ所属メンバー個人の部分的な情報（所有キャラクターは含まれない）
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class ClubMember {

	private PlayerIcon icon;	// PlayerIcon, optional
	private String tag;			// string, optional
	private String name;		// string, optional
	private int trophies;		// integer, optional
	private String role;		// string, optional
	private String nameColor;	// string, optional

}
