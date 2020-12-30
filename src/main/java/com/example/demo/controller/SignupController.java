package com.example.demo.controller;

import org.springframework.stereotype.Controller;

/**
 * システムユーザ登録時に呼ばれるコントローラだがソーシャルログインに一本化したため未使用
 * ソーシャルログイン以外のログイン経路を設定する場合はコメントアウトを解除
 * @author root1
 *
 */
@Controller
public class SignupController {

//	@Autowired
//	UserService userService;
//	@Autowired
//	PlayerService playerService;
//	@Autowired
//	ClubService clubService;
//	@Autowired
//	BrawlStarsAPIService brawlStarsService;
//	@Autowired
//	OwnBrawlerService ownBrawlerService;
//	@Autowired
//	BrawlerMasterService brawlerMasterService;
//
//	@GetMapping("/signup")
//	String getSignup(Model model) {
//		model.addAttribute("signupDto", new SignupDto());
//		return "signup";
//	}
//
//	@PostMapping("/signup")
//	String postSignup(@Validated SignupDto signupDto,
//						BindingResult result,
//						Model model) {
//
//		// バリデーションエラーの場合は登録ページを再度表示
//		if (result.hasErrors()) {
//			model.addAttribute("signupDto", signupDto);
//			return "signup";
//		}
//
//		// パスワード確認の不一致の場合は登録ページを再度表示
////		if (!StringUtils.equals(signupDto.getPassword(), signupDto.getPasswordConfirmation())) {
////			model.addAttribute("additionalMessage", "パスワード確認欄とパスワード欄の入力内容が一致しません。");
////			model.addAttribute("signupDto", signupDto);
////			return "signup";
////		}
//
//		// すでにユーザとして登録済みのプレイヤータグの場合は、入力画面にリダイレクト
//		if (userService.searchForSignup(signupDto.getPlayerTag()).size() == 1) {
//
//			// [要追記] フラッシュスコープへのリダイレクトメッセージ格納処理
//
//			return "redirect:/signin";
//		}
//
//		// 公式APIにアクセスして該当するプレイヤーデータを取得
//		PlayerInfoDto playerInfoDto = userService.getPlayerInfo(signupDto.getPlayerTag());
//
//		if (playerInfoDto == null) {
//			model.addAttribute("additionalMessage", "プレイヤータグが誤りです。");
//			model.addAttribute("signupDto", signupDto);
//			return "signup";
//		}
//
//		// 入力されたクラブタグと実際に所属しているクラブタグが異なる場合
//		if (!StringUtils.equals("#" + signupDto.getClubTag(), playerInfoDto.getClub().getTag())) {
//			model.addAttribute("additionalMessage", "ユーザタグまたはクラブタグが誤りです。");
//			model.addAttribute("signupDto", signupDto);
//
//			return "signup";
//		}
//
//		try {
//		    // DTO内のパスワードをSHA-256（SHA-2）アルゴリズムでハッシュ化
//	        MessageDigest sha256;
//			sha256 = MessageDigest.getInstance("SHA-256");
//	        byte[] sha256_result = sha256.digest(signupDto.getPassword().getBytes());
//	        signupDto.setPassword(String.format("%040x", new BigInteger(1, sha256_result)));
//
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//
//		// クラブ、プレイヤー、キャラクターのマスタを更新し、ユーザおよび所有キャラ関係をDB登録
//		clubService.create(playerInfoDto.getClub());
//		playerService.create(playerInfoDto);
//		userService.create(playerInfoDto, signupDto.getPassword());
//		brawlerMasterService.create(playerInfoDto.getBrawlers());
//		ownBrawlerService.create(playerInfoDto);
//
//		return "redirect:/signin";
//	}

}
