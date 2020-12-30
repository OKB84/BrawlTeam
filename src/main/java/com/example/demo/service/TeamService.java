package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.controller.dto.ChampionshipDto;
import com.example.demo.controller.dto.TeamDetailDto;
import com.example.demo.entity.TeamEntity;
import com.example.demo.mapper.BelongTeamMapper;
import com.example.demo.mapper.TeamMapper;

/**
 * チーム情報に関するサービスクラス
 * @author root1
 *
 */
@Service
public class TeamService {

	@Autowired
	TeamMapper teamMapper;

	@Autowired
	BelongTeamMapper belongTeamMapper;

	// 大会IDを元にチーム及びメンバー情報をまとめて大会情報として返す
	public ChampionshipDetailDto setChampionshipDetail(ChampionshipDetailDto championshipDetailDto) {

		// 返却用リストの土台作成
		List<TeamDetailDto> teamList = new ArrayList<TeamDetailDto>();

		// 参加チーム分ループしてチーム情報を取得
		for (TeamEntity teamEntity : teamMapper.search(championshipDetailDto.getId())) {

			TeamDetailDto teamDetailDto = new TeamDetailDto();
			teamDetailDto.setTeamNum(teamEntity.getTeamNum());
			teamDetailDto.setTeamName(teamEntity.getName());
			teamDetailDto.setMemberList(belongTeamMapper.search(teamEntity.getId()));

			teamList.add(teamDetailDto);
		}

		// 取得したチーム情報をセットした返却用リストをDTOにセット
		championshipDetailDto.setTeamList(teamList);

		return championshipDetailDto;
	}

	// 大会情報更新の際にteamテーブル及びbelong_teamテーブルの該当レコードを削除
	// 他のユーザが作成したデータが誤って削除されないように、ユーザIDを含んだDTOを使用
	public int delete(ChampionshipDto championshipDto) {

		return teamMapper.delete(championshipDto);
	}
}
