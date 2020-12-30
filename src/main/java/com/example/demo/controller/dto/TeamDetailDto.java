package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 個別のチャンピオンシップ詳細表示時に使用するクラス
 * @author root1
 *
 */
@Data
public class TeamDetailDto {

	private int teamNum;					// チーム番号
	private String teamName;				// チーム名
	private List<ChampionshipMemberDto> memberList;	// チームメンバーのプレイヤー名リスト
}
