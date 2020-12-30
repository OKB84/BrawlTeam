package com.example.demo.controller.dto;

import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;

import org.thymeleaf.util.StringUtils;

import com.example.demo.controller.dto.ChampionshipDto.CreateGroup;
import com.example.demo.controller.dto.ChampionshipDto.UpdateGroup;

import lombok.Data;

/**
 * チャンピオンシップ新規作成時に使用されるクラス
 * 作成したいチーム情報
 * @author root1
 *
 */
@Data
public class TeamDto {

	@Min(value = 1, groups = {CreateGroup.class, UpdateGroup.class})
	private int teamNum;	// チーム番号

	// チーム名（バリデーションはAssertTrueで条件付きで実施）
	private String name;

	// チームメンバー（バリデーションはAssertTrueで条件付きで実施）
	private List<ChampionshipMemberDto> member;

	// チームメンバーが1人以上の場合は、チーム名が設定されているかチェック
	@AssertTrue(groups = {CreateGroup.class, UpdateGroup.class}, message = "チーム名を入力してください。")
	public boolean isTeamNameNotRequriredOrExist() {

		if (member.size() > 0) {
			return !StringUtils.isEmpty(name) ? true : false;
		} else {
			return true;
		}
	}

	// チームメンバーが1人以上の場合は、チーム名が長すぎないかチェック
	@AssertTrue(groups = {CreateGroup.class, UpdateGroup.class}, message = "64文字以下で入力してください。")
	public boolean isTeamNameNotRequiredOrValidLength() {

		if (member.size() > 0) {
			return name.length() <= 64 ? true : false;
		} else {
			return true;
		}
	}

	// チーム名が入力されている場合は、チームメンバーが1人以上いるかチェック
	@AssertTrue(groups = {CreateGroup.class, UpdateGroup.class}, message = "メンバーを1人以上設定してください。")
	public boolean isTeamMemberNotRequiredOrExist() {

		if (name.length() > 0 && !StringUtils.isEmpty(name)) {
			return member.size() > 0 ? true : false;
		} else {
			return true;
		}

	}
}
