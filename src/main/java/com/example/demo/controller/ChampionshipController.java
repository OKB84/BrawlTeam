package com.example.demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.service.ChampionshipService;

/**
 * チャンピオンシップ関連で呼ばれるコントローラ
 * @author root1
 *
 */
@Controller
@RequestMapping("/championship")
public class ChampionshipController {

	@Autowired
	HttpSession session;

	@Autowired
	ChampionshipService championshipService;

	// 一覧画面の表示
	@GetMapping("/index")
	String getIndex() {

		return "/championship/index";
	}

	// 新規作成画面の表示
	@GetMapping("/create")
	String getCreate() {
		return "/championship/create";
	}

	// 編集画面の表示
	@GetMapping("/edit/{championshipId}")
	String getEdit(@PathVariable("championshipId") int championshipId,
					Model model) {

		ChampionshipDetailDto dto = championshipService.getDetail(championshipId);

		// ログイン中のユーザの作成した大会でなかった場合はリダイレクト
		if (dto == null) {
			return "redirect:/championship/index";
		}

		// Vueに渡す用の大会IDをセット
		model.addAttribute("championshipId", championshipId);

		return "/championship/edit";
	}
}
