package com.example.demo.controller.dto;

import lombok.Data;

/**
 * チャンピオンシップ情報登録バリデーションエラー発生時に使用されるクラス
 * リスト化して返すエラー情報オブジェクト
 * @author root1
 *
 */
@Data
public class ErrorResponseDto {

    private String keyName;		// キー名（大会日時、名称など）
    private Object keyValue;	// エラーとなった入力値
    private String message;		// エラーメッセージ

    // コンストラクタ
    public ErrorResponseDto(String keyName, Object keyValue, String message) {
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.message = message;
    }

}

