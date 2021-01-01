package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.config.RestTemplateConfig;
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

	@Autowired
	RestTemplateConfig config;

	// エンドポイントのベースURL
	public static final String BASE_URL = "https://api.brawlstars.com/v1/";

	// アクセスに必要な当システムのAPIキー
	public static final String API_KEY = System.getenv("BRAWL_STARS_API_KEY");

	// 環境（本番または開発）
	public static final String SPRING_PROFILES_ACTIVE = System.getenv("SPRING_PROFILES_ACTIVE");

	// プレイヤータグからプレイヤー情報を取得（１件のみ）
	public PlayerInfoDto getPlayerInfo(String playerTag) {

		try {

		    HttpHeaders headers = new HttpHeaders();
		    headers.add("Authorization", "Bearer " + API_KEY);
		    headers.add("Accept", "application/json");
		    HttpEntity<String> req = new HttpEntity<>(headers);

		    RestTemplate restTemplate = new RestTemplate();

		    if (!StringUtils.equals(SPRING_PROFILES_ACTIVE, "development")) {
			    config.customize(restTemplate);		// 本番環境では固定IPを利用
		    }

		    ResponseEntity<PlayerInfoDto> res = restTemplate.exchange(
		    	BASE_URL + "players/" + playerTag,		// プレイヤータグのシャープはパーセントにしないとエラーになる
		    	HttpMethod.GET, req, PlayerInfoDto.class
		    );
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

		    if (StringUtils.equals(SPRING_PROFILES_ACTIVE, "production")) {
			    config.customize(restTemplate);		// 本番環境では固定IPを利用
		    }

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
