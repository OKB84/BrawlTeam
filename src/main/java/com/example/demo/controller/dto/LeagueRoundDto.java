package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * @author root1
 * リーグ戦の各〜回戦の回戦数と対戦カードを保持するクラス
 */
@Data
public class LeagueRoundDto {

	private int roundNumber;				// ラウンド数
	private List<RoundMatchDto> matchList;	// 対戦カードのリスト
}
