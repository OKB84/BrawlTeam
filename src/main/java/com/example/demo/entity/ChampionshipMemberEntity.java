package com.example.demo.entity;

import java.util.Date;

import lombok.Data;


/**
 * championship_memberテーブルのレコードをマッピングする際に使用するクラス
 * 大会参加候補メンバーのユーザ、プレイヤー情報との紐付け情報
 * @author root1
 *
 */
@Data
public class ChampionshipMemberEntity {

	private int userId;					// ユーザID
	private String memberPlayerTag;		// 大会参加候補メンバーのプレイヤータグ
	private Date createdAt;				// レコード作成日時
	private Date updatedAt;				// レコード更新日時
}
