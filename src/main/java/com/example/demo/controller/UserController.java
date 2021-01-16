package com.example.demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	HttpSession session;

	@GetMapping("/profile")
	String getMypage() {

		// キャラクターマスタ編集権限の有無を確認するため管理者権限の有無をセッションに保存
		int admin = userService.search((Integer)session.getAttribute("userId")).getAdmin();
		session.setAttribute("admin", admin);

		return "account/profile";
	}

}
