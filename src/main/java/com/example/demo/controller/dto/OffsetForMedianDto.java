package com.example.demo.controller.dto;

import lombok.Data;

/**
 * 中央値算出に使用するクラス
 * @author root1
 *
 */
@Data
public class OffsetForMedianDto {

	private String type;		// タイプ（長距離、長距離メタなど）
	private int offsetNum;		// SQLのoffsetにセットする値
}
