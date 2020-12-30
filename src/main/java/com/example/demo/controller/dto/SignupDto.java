package com.example.demo.controller.dto;

import lombok.Data;

/**
 * システムユーザ登録時に使用するクラスだがソーシャルログインに一本化したため未使用
 * ソーシャルログイン以外のログイン経路を設定する場合はコメントアウトを解除
 * @author root1
 *
 */
@Data
public class SignupDto {

//    @NotBlank						// 空欄でないこと
//    @Size(max=12)					// 12文字以内であること（暫定）
//    @Pattern(regexp = "[A-Z0-9]*")	// 英大文字または数字であること
//	private String playerTag;
//
//    @NotBlank						// 空欄でないこと
//    @Size(max=12)					// 12文字以内であること（暫定）
//    @Pattern(regexp = "[A-Z0-9]*")	// 英大文字または数字であること
//	private String clubTag;
//
//    @NotBlank							// 空欄でないこと
//    @Size(min=4)						// 4文字以上であること（暫定）
//    @Pattern(regexp = "[a-zA-Z0-9]*")	// 英大文字または数字であること
//	private String password;
//
//    // passwordプロパティと相関チェックを行うため、アノテーションはつけない
//	private String passwordConfirmation;
//
//    @AssertTrue(message="パスワード確認欄とパスワード欄の入力内容が一致しません。")
//    public boolean isValidConfirm() {
//    	return StringUtils.equals(password, passwordConfirmation) ? true : false;
//    }
}
