package com.example.demo.controller.dto;

import java.util.Date;

import lombok.Data;

/**
 * 大会一覧表示の際に取得される各大会情報のクラス
 * 日付を年月日にフォーマットしている
 * @author root1
 *
 */
@Data
public class ChampionshipDateFormatedDto {

	private int id;				// 大会ID
	private String name;		// 大会名
	private int organizerId;	// 大会情報を作成したユーザのユーザID
	private String date;		// 開催日時（新規作成フォームから文字列でデータを受け取るため文字列型）
	private Date createdAt;		// レコード作成日時
	private Date updatedAt;		// レコード更新日時
}
