package com.example.demo.entity;

import java.util.Date;

import lombok.Data;


/**
 * userテーブルのレコードをマッピングする際に使用するクラス
 * 当システムユーザ情報
 * @author root1
 *
 */
@Data
public class UserEntity {

	private int id;					// ユーザID
	private String playerTag;		// ユーザが所有するゲームアカウントのプレイヤータグ
	private String socialMedia;		// 認証を行ったソーシャルメディアのサービス名
	private String socialMediaId;	// ソーシャルメディアのユーザID
	private String name;			// ソーシャルメディア上の名前
	private String email;			// ソーシャルメディアアカウントのメールアドレス
	private Date createdAt;			// レコード作成日時
	private Date updatedAt;			// レコード更新日時

}
