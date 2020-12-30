package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 公式APIからのプレイヤー情報返却時に使用されるクラス
 * 所有キャラクターの総合情報
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class BrawlerStat {

	List<StarPower> starPowers;		// StarPowerList, optional
	List<Accessory> gadgets;		// AccessoryList, optional
	int id;							// integer, optional
	int rank;						// integer, optional
	int trophies;					// integer, optional
	int highestTrophies;			// integer, optional
	int power;						// integer, optional
	String name;					// JsonLocalizedName, optional
}
