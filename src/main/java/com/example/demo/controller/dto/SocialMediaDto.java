package com.example.demo.controller.dto;

import lombok.Data;

/**
 * ソーシャルログイン時に使用されるクラス
 * ソーシャルメディアサーバから取得したユーザ情報等を格納
 * @author root1
 *
 */
@Data
public class SocialMediaDto {

	private String socialMedia;		// ソーシャルメディアのサービス名
	private String socialMediaId;	// ユーザID
	private String name;			// ユーザ名
	private String email; 			// メールアドレス
}
