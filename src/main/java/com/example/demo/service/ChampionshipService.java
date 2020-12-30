package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.dto.ChampionshipCreateDto;
import com.example.demo.controller.dto.ChampionshipDateFormatedDto;
import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.controller.dto.ChampionshipDto;
import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.controller.dto.TeamDto;
import com.example.demo.entity.BelongTeamEntity;
import com.example.demo.entity.ChampionshipEntity;
import com.example.demo.entity.TeamEntity;
import com.example.demo.mapper.BelongTeamMapper;
import com.example.demo.mapper.ChampionshipMapper;
import com.example.demo.mapper.TeamMapper;

/**
 * チャンピオンシップ基本情報に関するサービスクラス
 * @author root1
 *
 */
@Service
public class ChampionshipService {

	@Autowired
	ChampionshipMapper championshipMapper;
	@Autowired
	TeamMapper teamMapper;
	@Autowired
	BelongTeamMapper belongTeamMapper;
	@Autowired
	HttpSession session;

	// 新規作成（チャンピオンだけ、チームだけなどデータが中途半端に登録されないようにトランザクション処理）
	@Transactional
	public int create(ChampionshipDto championshipDto) throws DuplicateKeyException {

		// 大会、チーム、メンバーの各レコード合計登録件数
		int totalCreateCount = 0;

		championshipDto.setOrganizerId((Integer)session.getAttribute("userId"));

		// ChampionshipCreateDtoへの詰め替え
		ChampionshipCreateDto championshipCreateDto = refillChampionshipCreateDto(championshipDto);

		// championshipテーブルへのレコード登録
		totalCreateCount = championshipMapper.create(championshipCreateDto);

		// レコードの登録失敗時は0を返して処理を終了
		if (totalCreateCount == 0) {
			return 0;
		}

		// championshipテーブルに登録されたレコードのIDを取得しDTOにセット
		championshipDto.setChampionshipId(championshipMapper.getCreatedId(championshipCreateDto));

		// チーム及び所属メンバーレコードを作成
		totalCreateCount += createTeamAndMember(championshipDto);

		return totalCreateCount;
	}

	// ユーザが作成したチャンピオンシップ一覧を取得
	public List<ChampionshipDateFormatedDto> search(int userId) {

		List<ChampionshipDateFormatedDto> list = new ArrayList<ChampionshipDateFormatedDto>();

		for (ChampionshipEntity entity : championshipMapper.search(userId)) {

			ChampionshipDateFormatedDto dto = new ChampionshipDateFormatedDto();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d (E) HH:mm");

			dto.setId(entity.getId());
			dto.setName(entity.getName());
			dto.setOrganizerId(entity.getOrganizerId());
			dto.setDate(dateFormat.format(entity.getDate()));
			dto.setCreatedAt(entity.getCreatedAt());
			dto.setUpdatedAt(entity.getUpdatedAt());
			list.add(dto);
		}

		return list;
	}

	// 詳細情報取得（チームやメンバー等を含む）
	public ChampionshipDetailDto getDetail(int championshipId) {

		// Entityに大会IDとログイン中ユーザIDをセット
		ChampionshipCreateDto championshipCreateDto = new ChampionshipCreateDto();
		championshipCreateDto.setId(championshipId);
		championshipCreateDto.setOrganizerId((Integer)session.getAttribute("userId"));

		List<ChampionshipDetailDto> list = championshipMapper.getDetail(championshipCreateDto);

		for (ChampionshipDetailDto dto : list) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("M/d (E)");
			dto.setDateStr(dateFormat.format(dto.getDate()));
		}

		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	// 更新
	public int update(ChampionshipDto championshipDto) {

		// 日付と時刻を合わせてカラムに保存するためDTO詰め替え
		// 文字列型のまま保存するためEntityには詰め替えない
		ChampionshipCreateDto championshipCreateDto = refillChampionshipCreateDto(championshipDto);

		// 大会、チーム、所属プレイヤーの各レコード合計登録・更新件数
		int totalCreateCount = 0;

		// 大会基本情報を更新
		totalCreateCount += championshipMapper.update(championshipCreateDto);

		// 既存のチーム及び所属プレイヤー情報を削除
		totalCreateCount += teamMapper.delete(championshipDto);

		// チーム及び所属プレイヤー情報を登録
		totalCreateCount += createTeamAndMember(championshipDto);

		return totalCreateCount;
	}

	// 削除（複数件一括）
	public int delete(List<ChampionshipDateFormatedDto> list) {

		int successCount = 0;

		for (ChampionshipDateFormatedDto dto : list) {

			// 他のユーザのデータを削除できないようにログイン中のユーザIDをセット
			dto.setOrganizerId((Integer)session.getAttribute("userId"));
			successCount += championshipMapper.delete(dto);
		}

		return successCount;
	}

	// 削除（複数件一括）
	public int delete(ChampionshipDto dto) {

		int successCount = 0;

		// 他のユーザのデータを削除できないようにログイン中のユーザIDをセット
		dto.setOrganizerId((Integer)session.getAttribute("userId"));
		successCount += championshipMapper.deleteBeforeUpdate(dto);

		return successCount;
	}

	// 大会情報の新規作成及び更新時にチーム及びメンバー情報を登録
	private int createTeamAndMember(ChampionshipDto championshipDto) {

		int successCount = 0;	// DB更新件数

		TeamEntity teamEntity = new TeamEntity();
		teamEntity.setChampionshipId(championshipDto.getChampionshipId());

		// 各チームを登録
		for (TeamDto teamDto : championshipDto.getTeamList()) {

			// チームメンバーが0人の場合は登録処理をスキップ
			if (teamDto.getMember().size() == 0) {
				continue;
			}

			// Entityへの詰め替え
			teamEntity.setName(teamDto.getName());
			teamEntity.setTeamNum(teamDto.getTeamNum());

			// チーム登録成功時はメンバーも登録
			if(teamMapper.create(teamEntity) == 1) {
				successCount++;

				// 登録したチームの自動生成IDを取得
				int teamId = teamMapper.getCreatedId(teamEntity);

				// チーム、プレイヤー対応関係テーブルのレコード作成用Entityの作成
				BelongTeamEntity belongTeamEntity = new BelongTeamEntity();
				belongTeamEntity.setTeamId(teamId);

				// チーム、プレイヤー対応関係テーブルのレコード作成
				for (ChampionshipMemberDto playerJoinListDto : teamDto.getMember()) {
					belongTeamEntity.setPlayerTag(playerJoinListDto.getPlayerTag());
					successCount += belongTeamMapper.create(belongTeamEntity);
				}
			}
		}

		return successCount;
	}

	// ChampionshipCreateDtoへの詰め替え
	private ChampionshipCreateDto refillChampionshipCreateDto(ChampionshipDto championshipDto) {

		// 日付と時刻を合わせてカラムに保存するためDTO詰め替え
		// 文字列型のまま保存するためEntityには詰め替えない
		ChampionshipCreateDto championshipCreateDto = new ChampionshipCreateDto();
		championshipCreateDto.setName(championshipDto.getName());
		championshipCreateDto.setDate(championshipDto.getDate() + " "
									+ championshipDto.getTime() + ":00");
		championshipCreateDto.setId(championshipDto.getChampionshipId());
		championshipCreateDto.setOrganizerId(championshipDto.getOrganizerId());

		return championshipCreateDto;
	}
}
