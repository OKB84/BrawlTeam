package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.controller.dto.ClubMember;
import com.example.demo.controller.dto.ClubPlayerAPIDto;
import com.example.demo.controller.dto.PlayerInfoDto;

/**
 * 公式APIと通信を行うサービスクラス
 * @author root1
 *
 */
@Service
public class BrawlStarsAPIService {

	// エンドポイントのベースURL
	public static final String BASE_URL = "https://api.brawlstars.com/v1/";

	// アクセスに必要な当システムのAPIキー
	public static final String API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjgyYzc3YWM1LTZiZGMtNDcxMC1hY2FkLTgwNWU4YTFmZWE0NyIsImlhdCI6MTYwNTcxMzY2MCwic3ViIjoiZGV2ZWxvcGVyL2U4ODRjMGY3LTk0NTMtZGQxYi1mZTkxLTc1NTEyNmViOWIyNiIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTUzLjI0Ni4xMzQuMTEyIl0sInR5cGUiOiJjbGllbnQifV19._SdrBRrkLlz_FrHBCj8A3gVd3Mv2batOYYQtIrUPbM70B8fgqMBdKax4poC_oFzTefTbNzY3Nov2-HqIEuZwsw";

	// プレイヤータグからプレイヤー情報を取得（１件のみ）
	public PlayerInfoDto getPlayerInfo(String playerTag) {

		try {
		    HttpHeaders headers = new HttpHeaders();
		    headers.add("Authorization", "Bearer " + API_KEY);
		    headers.add("Accept", "application/json");
		    HttpEntity<String> req = new HttpEntity<>(headers);
		    RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<PlayerInfoDto> res = restTemplate.exchange(
		    	BASE_URL + "players/" + playerTag,		// プレイヤータグのシャープはパーセントにしないとエラーになる
		    	HttpMethod.GET, req, PlayerInfoDto.class
		    );
		    System.out.println(res.getBody());
		    return res.getBody();

		} catch (HttpClientErrorException e) {

			// playerTagに該当するプレイヤーが見つからなかった場合
			e.printStackTrace();
			return null;

		}
	}

	// プレイヤータグからプレイヤー情報を取得（一括取得）
	public List<PlayerInfoDto> getPlayerInfo(List<String> playerTagList){

		List<PlayerInfoDto> playerInfoList = new ArrayList<PlayerInfoDto>();

		for (String playerTag : playerTagList) {
			PlayerInfoDto dto = getPlayerInfo(playerTag);
			if (dto != null) {
				playerInfoList.add(dto);
			}
		}

		return playerInfoList;
	}

	// 所属クラブのプレイヤータグからプレイヤー情報を取得（一括取得）
	public List<PlayerInfoDto> getClubMemberInfo(List<ClubMember> clubMemberList){

		List<PlayerInfoDto> playerInfoList = new ArrayList<PlayerInfoDto>();

		for (ClubMember member : clubMemberList) {
			PlayerInfoDto dto = getPlayerInfo(member.getTag().replace("#", "%"));
			if (dto != null) {
				playerInfoList.add(dto);
			}
		}

		return playerInfoList;
	}

	// クラブタグからクラブに所属しているプレイヤー情報を取得
	public ClubPlayerAPIDto getClubPlayerInfo(String clubTag) {

		try {
		    HttpHeaders headers = new HttpHeaders();
		    headers.add("Authorization", "Bearer " + API_KEY);
		    headers.add("Accept", "application/json");
		    HttpEntity<String> req = new HttpEntity<>(headers);
		    RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<ClubPlayerAPIDto> res = restTemplate.exchange(
		    	BASE_URL + "clubs/" + clubTag + "/members",		// クラブタグのシャープはパーセントにしないとエラーになる
		    	HttpMethod.GET, req, ClubPlayerAPIDto.class
		    );
		    return res.getBody();

		} catch (HttpClientErrorException e) {

			// playerTagに該当するプレイヤーが見つからなかった場合
			e.printStackTrace();
			return null;

		}
	}
}
