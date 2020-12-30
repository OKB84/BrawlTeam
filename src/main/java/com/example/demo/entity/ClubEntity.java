package com.example.demo.entity;
import java.util.Date;

import lombok.Data;

/**
 * clubテーブルのレコードをマッピングする際に使用するクラス
 * ゲーム内クラブ情報
 * @author root1
 *
 */
@Data
public class ClubEntity {

	private String tag;			// クラブタグ
	private String name;		// 名称
	private Date createdAt;		// レコード作成日時
	private Date updatedAt;		// レコード更新日時
}
