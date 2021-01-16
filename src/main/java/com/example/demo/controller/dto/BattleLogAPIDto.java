package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 公式APIからのバトルログ取得時に使用されるクラス
 * バトルログの一覧情報
 * @author root1
 *
 */
@Data
public class BattleLogAPIDto {

	private List<BattleLog> items;		// なぜかJSONでitemsという名前で送られてくる
}
