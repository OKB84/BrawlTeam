package com.example.demo.controller.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 個別のチャンピオンシップ詳細表示時に使用するクラス
 * @author root1
 *
 */
@Data
public class ChampionshipDetailDto {

	private int id;							// 大会ID
	private String championshipName;		// 大会名
	private Date date;						// 開催日時（Date型）
	private String dateStr;					// 開催日時（String型）
	private List<TeamDetailDto> teamList;	// 参加チームリスト
}
