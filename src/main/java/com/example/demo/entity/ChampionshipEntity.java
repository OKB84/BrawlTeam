package com.example.demo.entity;
import java.util.Date;

import lombok.Data;

/**
 * championshipテーブルのレコードをマッピングする際に使用するクラス
 * チャンピオンシップ情報
 * @author root1
 *
 */
@Data
public class ChampionshipEntity {

	private int id;				// 大会ID
	private String name;		// 大会名
	private int organizerId;	// 大会情報を作成したユーザのユーザID
	private Date date;		// 開催日時（新規作成フォームから文字列でデータを受け取るため文字列型）
	private Date createdAt;		// レコード作成日時
	private Date updatedAt;		// レコード更新日時
}
