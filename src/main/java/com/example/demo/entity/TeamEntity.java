package com.example.demo.entity;

import java.util.Date;

import lombok.Data;

/**
 * teamテーブルのレコードをマッピングする際に使用するクラス
 * チャンピオンシップに登録された出場チーム
 * @author root1
 *
 */
@Data
public class TeamEntity {

	private int id;					// チームID
	private int championshipId;		// チャンピオンシップID
	private int teamNum;			// チーム番号（チャンピオンシップに固有）
	private String name;			// チーム名
	private Date createdAt;			// レコード作成日時
	private Date updatedAt;			// レコード更新日時
}
