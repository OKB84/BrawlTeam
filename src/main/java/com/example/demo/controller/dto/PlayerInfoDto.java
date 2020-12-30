package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 公式APIからのプレイヤー情報返却時に使用されるクラス
 * プレイヤーの総合情報
 * プロパティのコメントは公式APIドキュメントの記載
 * @author root1
 *
 */
@Data
public class PlayerInfoDto {

	PlayerClub club;				// PlayerClub, optional
	boolean isQualifiedFromChampionshipChallenge;	// boolean, optional
//	int 3vs3Victories;				// 数字はじまりの変数名は定義できないため実装見送り
	PlayerIcon icon;				// PlayerIcon, optional
	String tag;						// string, optional
	String name;					// string, optional
	int trophies;					// integer, optional
	int expLevel;					// integer, optional
	int expPoints;					// integer, optional
	int highestTrophies;			// integer, optional
	int powerPlayPoints;			// integer, optional
	int highestPowerPlayPoints;		// integer, optional
	int soloVictories;				// integer, optional
	int duoVictories;				// integer, optional
	int bestRoboRumbleTime;			// integer, optional
	int bestTimeAsBigBrawler;		// integer, optional
	List<BrawlerStat> brawlers;		// BrawlerStatList, optional
	String nameColor;				// string, optional
}
