package com.example.demo.controller.dto;

import java.util.Date;

import lombok.Data;

/**
 * チャンピオンシップリマインドメール発出時に使用されるクラス
 * @author root1
 *
 */
@Data
public class ChampionshipReminderDto {

	private String championshipName;	// 大会名
	private Date date;					// 開催日時（Date型）
	private String playerTag;			// ユーザが所有するゲームアカウントのプレイヤータグ
	private String socialMedia;			// 認証を行ったソーシャルメディアのサービス名
	private String socialMediaId;		// ソーシャルメディアのユーザID
	private String userName;			// ソーシャルメディア上の名前
	private String email;				// ソーシャルメディアアカウントのメールアドレス
	private int admin;					// 管理者権限の有無（0:なし、1:あり）

}
