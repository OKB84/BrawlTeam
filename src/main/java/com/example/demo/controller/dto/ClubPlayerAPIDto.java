package com.example.demo.controller.dto;

import java.util.List;

import lombok.Data;

/**
 * 公式APIからのクラブ所属メンバー情報返却時で使用されるクラス
 * クラブ所属メンバーの一覧情報
 * @author root1
 *
 */
@Data
public class ClubPlayerAPIDto {

	private List<ClubMember> items;		// なぜかJSONでitemsという名前で送られてくる

}
