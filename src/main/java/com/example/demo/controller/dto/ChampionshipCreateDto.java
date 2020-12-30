package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 大会新規登録時に使用されるクラス
 * @author root1
 *
 */
@Data
public class ChampionshipCreateDto {

	private int id;				// 大会ID
	private String name;		// 大会名
	private int organizerId;	// 大会情報を作成したユーザのユーザID
	private String date;		// 開催日時（新規作成フォームから文字列でデータを受け取るため文字列型）
}
