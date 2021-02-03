package com.example.demo.controller.dto;

import lombok.Data;

/**
 * @author root1
 * リーグ戦のラウンドの各対戦カード
 */
@Data
public class RoundMatchDto {

	// team1とteam2が対戦する
	private String teamName1;
	private String teamName2;
}
