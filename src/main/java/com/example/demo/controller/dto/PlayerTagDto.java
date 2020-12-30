package com.example.demo.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * ユーザ自身が持つゲームアカウントのプレイヤータグをユーザテーブルに登録する際に使用されるクラス
 * フォーム送信の際にサーバサイドでバリデーションを実施したいため、わざわざDTOとして作成した
 * @author root1
 *
 */
@Data
public class PlayerTagDto {

    @NotBlank(message = "プレイヤータグを入力してください。")	// 空欄でないこと
    @Size(max=12, message = "12文字以内で入力してください。")	// 12文字以内であること（暫定）
    @Pattern(regexp = "[A-Z0-9]*", message = "半角英大文字または半角数字で入力してください。")	// 英大文字または数字であること
	private String playerTag;		// プレイヤータグ

}
