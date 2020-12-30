package com.example.demo.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.service.BrawlStarsAPIService;
import com.example.demo.service.BrawlerMasterService;
import com.example.demo.service.ChampionshipMemberService;
import com.example.demo.service.OwnBrawlerService;
import com.example.demo.service.PlayerService;
import com.example.demo.service.UserService;

/**
 * 大会参加候補メンバー関連で呼ばれるコントローラ
 * @author root1
 *
 */
@Controller
@RequestMapping("/championship-member")
public class ChampionshipMemberController {

	@Autowired
	HttpSession session;
	@Autowired
	UserService userService;
	@Autowired
	PlayerService playerService;
	@Autowired
	BrawlStarsAPIService apiService;
	@Autowired
	OwnBrawlerService ownBrawlerService;
	@Autowired
	BrawlerMasterService brawlerMasterService;
	@Autowired
	ChampionshipMemberService ｍemberService;

	// 一覧表示
	@GetMapping("/index")
	String getIndex(Model model) {

		// ログイン中のユーザの大会参加候補メンバーのプレイヤー情報を取得
		List<ChampionshipMemberDto> list = playerService.getMembersPlayerInfo();
		model.addAttribute("playerList", list);
		return "championship-member/index";
	}

	// メンバー追加画面の初期表示
	@GetMapping("/create")
	String getCreate() {

		return "championship-member/create";
	}

}
