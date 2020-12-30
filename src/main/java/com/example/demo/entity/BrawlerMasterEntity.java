package com.example.demo.entity;
import java.util.Date;

import lombok.Data;

/**
 * brawler_masterテーブルのレコードをマッピングする際に使用するクラス
 * キャラクターマスタ情報
 * @author root1
 *
 */
@Data
public class BrawlerMasterEntity {

	private int id;				// キャラクターID
	private String name;		// キャラクター名
	private String type;		// タイプ（長距離、長距離メタなど）
	private Date createdAt;		// レコード作成日時
	private Date updatedAt;		// レコード更新日時
}
