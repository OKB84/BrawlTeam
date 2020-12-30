package com.example.demo.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.controller.dto.SocialMediaDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * ログイン、ログアウト時に呼ばれるコントローラ
 * @author root1
 *
 */
@Controller
public class SigninController {

	// Googleログインに必要なクライアントID
	final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");

	@Autowired
	UserService userService;

	@Autowired
	HttpSession session;

	// ログイン前のトップページ初期表示
	@GetMapping("/")
	String index() {
		return "toppage/index";
	}

	// ログアウト
	@GetMapping("/signout")
	String getSignout() {
		session.invalidate();	// セッションをクリア
		return "redirect:/";
	}

	// ソーシャルログイン
	// ①ユーザ認証完了後に返却されたIDトークンを元にユーザのGoogleアカウント情報を取得
	// ②DBに登録されていないユーザの場合はDB登録
	// ③マイページにリダイレクト
	@PostMapping("/google/signin")
	String tokenSignin(String idTokenString) throws GeneralSecurityException, IOException {

		// ユーザ認証完了後に返却されたIDトークンを元にユーザのGoogleアカウント情報を取得
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
			    .setAudience(Collections.singletonList(CLIENT_ID))
			    .build();
		GoogleIdToken idToken = verifier.verify(idTokenString);

		// Google IDトークン取得失敗時はログイン画面にリダイレクト
		if (idToken == null) {
			System.out.println("Invalid ID token.");
			return "redirect:/signin";
		}

		// 受信データ本体を抽出
		Payload payload = idToken.getPayload();

		// DTOに詰め替え
		SocialMediaDto socialMediaDto = new SocialMediaDto();
		socialMediaDto.setSocialMedia("google");
		socialMediaDto.setSocialMediaId(payload.getSubject());
		socialMediaDto.setName((String)payload.get("name"));
		socialMediaDto.setEmail((String)payload.getEmail());

		// DBにGoogleアカウント認証済みユーザとして登録されているか検索
		UserEntity entity = userService.search(socialMediaDto);

		// すでに登録済みのユーザの場合は、セッションスコープにユーザIDを保存
		if (entity != null) {
			session.setAttribute("userId", entity.getId());
		} else {
			// ユーザをDB登録し、登録成功時はセッションスコープにユーザIDを保存
			if (userService.create(socialMediaDto) == 1) {
				entity = userService.search(socialMediaDto);
				session.setAttribute("userId", entity.getId());
			}
		}

		// セッションスコープにユーザIDが保存されていなければ、ログインチェック機能によりログインページにリダイレクト
		return "redirect:/championship/index";
	}
}
