package com.example.demo.entity;
import java.util.Date;

import lombok.Data;

/**
 * belong_teamテーブルのレコードをマッピングする際に使用するクラス
 * プレイヤーの所属チーム紐付け情報
 * @author root1
 *
 */
@Data
public class BelongTeamEntity {

	private int teamId;			// チームID
	private String playerTag;	// プレイヤータグ
	private Date createdAt;		// レコード作成日時
	private Date updatedAt;		// レコード更新日時
}
