package com.example.demo.controller.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * チャンピオンシップ登録のPOSTリクエスト送信で使用するクラス
 * @author root1
 *
 */
@Data
public class ChampionshipDto {

	@NotNull(groups = {UpdateGroup.class}, message = "編集対象の大会が見つかりません。")
	private int championshipId;		// 大会ID

	@Valid
	@Size(min = 1)
	private List<TeamDto> teamList;		// チームリスト（空のチーム含む：ChampionshipServiceで登録前に排除）

	@NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "日付を入力してください。")
	private String date;	// 大会開催日付

	@NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "時刻を入力してください。")
	private String time;	// 大会開催時刻

	@Size(max = 20, groups = {CreateGroup.class, UpdateGroup.class}, message = "20文字以下で入力してください。")
	@NotBlank(groups = {CreateGroup.class, UpdateGroup.class}, message = "名称を入力してください。")
	private String name;	// 大会名

	private int organizerId;

	// 過去日付の場合はバリデーションエラー
	@AssertTrue(groups = {CreateGroup.class, UpdateGroup.class}, message = "過去の日時は登録できません。")
	public boolean isValidDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		try {
			// 現在日時を取得して入力日時が過去であればfalseをリターン
			Date inputDate = format.parse(date + " " + time);
			Date currentDate = new Date();
			return currentDate.compareTo(inputDate) <= 0 ? true : false;

		} catch (ParseException e) {
			// フォーマット違反の日付だった場合
			return false;
		}
	}

	// チーム数が0の場合はバリデーションエラー
	@AssertTrue(groups = {CreateGroup.class, UpdateGroup.class}, message = "チームを1つ以上登録してください。")
	public boolean isNotMemberEmpty() {
		int numOfMember = 0;
		for (TeamDto dto : teamList) {
			if (dto.getMember().size() > 0) {
				numOfMember++;
			}
		}
		return numOfMember > 0 ? true : false;
	}

	// 新規登録時のバリデーショングループ
	public interface CreateGroup {
	}

	// 編集時のバリデーショングループ
	public interface UpdateGroup {
	}
}

