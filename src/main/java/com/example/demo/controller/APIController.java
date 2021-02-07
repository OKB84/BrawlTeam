package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.example.demo.controller.dto.ChampionshipDateFormatedDto;
import com.example.demo.controller.dto.ChampionshipDetailDto;
import com.example.demo.controller.dto.ChampionshipDto;
import com.example.demo.controller.dto.ChampionshipDto.CreateGroup;
import com.example.demo.controller.dto.ChampionshipDto.UpdateGroup;
import com.example.demo.controller.dto.ChampionshipMemberDto;
import com.example.demo.controller.dto.ClubPlayerAPIDto;
import com.example.demo.controller.dto.ErrorResponseDto;
import com.example.demo.controller.dto.LeagueRoundDto;
import com.example.demo.controller.dto.PlayerDetailDto;
import com.example.demo.controller.dto.PlayerInfoDto;
import com.example.demo.controller.dto.PlayerTagDto;
import com.example.demo.entity.BrawlerMasterEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.BrawlStarsAPIService;
import com.example.demo.service.BrawlerMasterService;
import com.example.demo.service.ChampionshipMemberService;
import com.example.demo.service.ChampionshipService;
import com.example.demo.service.ClubService;
import com.example.demo.service.OwnBrawlerService;
import com.example.demo.service.PlayerService;
import com.example.demo.service.PrintService;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;


/**
 * 内部API
 * @author root1
 *
 */
@RestController
@RequestMapping("/api")
public class APIController {

	@Autowired
	UserService userService;
	@Autowired
	PlayerService playerService;
	@Autowired
	OwnBrawlerService ownBrawlerService;
	@Autowired
	BrawlStarsAPIService brawlStarsAPIService;
	@Autowired
	ChampionshipService championshipService;
	@Autowired
	ClubService clubService;
	@Autowired
	TeamService teamService;
	@Autowired
	BrawlerMasterService brawlerMasterService;
	@Autowired
	ChampionshipMemberService championshipMemberService;
	@Autowired
	PrintService printService;

	@Autowired
	HttpSession session;

	// 新規作成画面の初期表示で大会参加候補メンバー一覧を表示
	@GetMapping("/member/all")
	List<ChampionshipMemberDto> getPlayerList() {

		List<ChampionshipMemberDto> list = playerService.getMembersPlayerInfo();

		return list;
	}

	// 大会参加候補メンバーのプレイヤー情報を最新状態に更新
	// このメソッドは短時間に何回も呼ばれると通信拒否される可能性があるので注意
	@GetMapping("/member/update")
	List<ChampionshipMemberDto> getPlayerListUpdate() {

		// ログイン中のユーザの大会参加候補メンバーを取得し、公式APIから最新情報を取得
		List<String> playerTagList = championshipMemberService.search();
		List<PlayerInfoDto> playerInfoList = brawlStarsAPIService.getPlayerInfo(playerTagList);

		// 公式APIから取得した最新情報を元にプレイヤー情報、所有キャラクター情報を更新
		playerService.update(playerInfoList);
		ownBrawlerService.create(playerInfoList);

		return getPlayerList();		// DBに登録されているデータを返却
	}

	// 大会参加候補メンバーを削除
	@PostMapping("/member/delete")
	ResponseEntity<Object> postDeleteMember(@RequestBody PlayerTagDto playerTagDto) {

		if(championshipMemberService.delete(playerTagDto.getPlayerTag()) > 0) {
			return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
		} else {
			return createErrorResponse("delete", null, "削除に失敗しました");
		}
	}

	// チャンピオンシップの新規作成
	@PostMapping("/championship/create")
	ResponseEntity<Object> postCreate(
						@Validated(CreateGroup.class) @RequestBody ChampionshipDto championshipDto,
						BindingResult result) {

		// バリデーションエラー時は、エラー内容を返却
		if (result.hasErrors()) {
			return createErrorResponse(result);
		}

		try {
			// 大会情報、チーム情報、チームとプレイヤーの対応関係情報を登録
			int totalCreateCount = championshipService.create(championshipDto);

		} catch (DuplicateKeyException e) {
			// ユーザが登録済みの大会と日時が同一だった場合
			List<ErrorResponseDto> list = new ArrayList<ErrorResponseDto>();
				list.add(new ErrorResponseDto(
											"date",
											championshipDto.getDate(),
											"すでに登録されている日時です。"));
			return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
	}

	// 大会更新
	@PostMapping("/championship/update")
	ResponseEntity<Object> postUpdate(
						@Validated(UpdateGroup.class) @RequestBody ChampionshipDto championshipDto,
						BindingResult result) {

		// バリデーションエラー時は、エラー内容を返却
		if (result.hasErrors()) {
			return createErrorResponse(result);
		}

		// ログイン中のユーザIDをDTOに追加
		championshipDto.setOrganizerId((Integer)session.getAttribute("userId"));

		// 大会情報を更新の上、既存のチーム及び所属プレイヤー情報を削除してから登録
		int totalCreateCount = championshipService.update(championshipDto);

		return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
	}


	// 大会一覧の初期表示
	@GetMapping("/championship/all")
	List<ChampionshipDateFormatedDto> getAllChampionship() {

		// ログイン中のユーザが作成した大会リストを取得
		return championshipService.search((Integer)session.getAttribute("userId"));
	}

	// 大会詳細表示
	@GetMapping("/championship/show/{id}")
	ChampionshipDetailDto getChampionshipDetail(@PathVariable("id") int id) {

		// URLの大会IDに一致する且つログイン中のユーザが作成した大会情報詳細を取得
		ChampionshipDetailDto championshipDetailDto = championshipService.getDetail(id);

		if (championshipDetailDto == null) {
			return null;
		}

		return teamService.setChampionshipDetail(championshipDetailDto);
	}

	// リーグ戦の対戦表ダウンロード
	@GetMapping("/championship/league/{id}")
	public void getLeagueMatchPattern(@PathVariable("id") int id, HttpServletResponse response) {

		// URLの大会IDに一致する且つログイン中のユーザが作成した大会情報詳細を取得
		ChampionshipDetailDto championshipDetailDto = championshipService.getDetail(id);

		if (championshipDetailDto == null) {
			return;
		}

		championshipDetailDto = teamService.setChampionshipDetail(championshipDetailDto);

		// リーグ戦の対戦リストを作成
		List<LeagueRoundDto> leagueMatchList = championshipService.createLeagueMatchPattern(championshipDetailDto);

		// 対戦表を印刷
		printService.printLeagueMatchPattern(leagueMatchList, response);
	}

	// チームメンバー表ダウンロード
	@GetMapping("/championship/download/{id}")
	public void getDocument(@PathVariable("id") int id, HttpServletResponse response) {

		// URLの大会IDに一致する且つログイン中のユーザが作成した大会情報詳細を取得
		ChampionshipDetailDto championshipDetailDto = championshipService.getDetail(id);

		if (championshipDetailDto == null) {
			return;
		}

		championshipDetailDto = teamService.setChampionshipDetail(championshipDetailDto);

		printService.printTeamList(championshipDetailDto, response);
	}

	// メンバー詳細取得
	@GetMapping("/member/show/{playerTag}")
	PlayerDetailDto getMemberDetail(@PathVariable("playerTag") String playerTag) {

		// 「キャラタイプ別の最多トロフィー中央値」以外の詳細情報を取得
		PlayerDetailDto dto = playerService.getPlayerBasicDetail("%" + playerTag);

		try {
			dto = playerService.setPlayerBattleInfo(dto);
			// 中央値を追加して返却
			return ownBrawlerService.setMedian(dto);
		} catch (HttpClientErrorException e) {
			return dto;
		}
	}

	// 大会情報削除
	@PostMapping("/championship/delete")
	ResponseEntity<Object> postDeleteChampionship(@RequestBody List<ChampionshipDateFormatedDto> list) {

		int deleteCount = championshipService.delete(list);

		System.out.println(deleteCount);
		return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
	}

	// メンバー追加（プレイヤー情報、キャラクター情報も同時に更新）
	@PostMapping("/member/create")
	ResponseEntity<Object> postCreate(@Validated @RequestBody PlayerTagDto playerTagDto,
										BindingResult result) {

		// バリデーションエラー時は、エラー内容を返却
		if (result.hasErrors()) {
			return createErrorResponse(result);
		}

		// 公式APIからプレイヤー情報を取得するために、入力されたプレイヤータグの先頭#を%にする
		String playerTag = "%" + playerTagDto.getPlayerTag();

		// すでにメンバー追加済みのメンバーであればエラーを返す
		if (championshipMemberService.isExistedMember(playerTag)) {
			return createErrorResponse("playerTag", playerTag, "すでに追加されているメンバーです。");
		}

		// 公式APIからプレイヤー情報を取得
		PlayerInfoDto dto = brawlStarsAPIService.getPlayerInfo(playerTag);

		// 公式APIからプレイヤー情報を取得できなかった場合
		if (dto == null) {
			return createErrorResponse("playerTag", playerTag, "プレイヤーが見つかりませんでした。");
		}

		int registCount = 0;	// DB更新件数

		// プレイヤー情報、キャラクター情報、大会参加候補メンバー情報を登録
		registCount += playerService.createOnDuplicateKeyUpdate(dto)
						+ brawlerMasterService.create(dto.getBrawlers())
						+ ownBrawlerService.create(dto)
						+ championshipMemberService.create(playerTag);

		if (registCount > 0) {
			// プレイヤー情報、キャラクター情報はDB登録済みだった場合、更新件数は最低1件
			return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
		} else {
			// 大会参加候補メンバー登録すら失敗していた場合
			return createErrorResponse("playerTag", playerTag, "メンバー追加に失敗しました。再度お試しください。");
		}
	}

	// 所属クラブ内の全プレイヤーデータを大会参加候補メンバーに追加
	@PostMapping("/member/import")
	ResponseEntity<Object> importClubPlayer() {

		// ログイン中のユーザが持つ自身のゲームアカウントのプレイヤータグをDBから取得
		String playerTag = userService
							.search((Integer)session.getAttribute("userId"))
							.getPlayerTag();

		if (StringUtils.isEmpty(playerTag)) {
			// ユーザ情報にプレイヤータグが含まれていない場合
			return createErrorResponse("import", null, "アカウント設定でプレイヤータグを登録してください。");
		}

		// ログイン中のユーザが所属するクラブのタグを取得
		String clubTag = playerService.search(playerTag).getClubTag();

		if (StringUtils.isEmpty(clubTag)) {
			// クラブに所属していないプレイヤーだった場合
			return createErrorResponse("import", null, "所属クラブがありません。");
		}

		// 公式APIからクラブ所属メンバーの一覧を取得
		// 公式APIからのプレイヤー情報の際にプレイヤータグの#を%にしていないとエラーになる
		ClubPlayerAPIDto clubPlayerAPIDto = brawlStarsAPIService.getClubPlayerInfo(clubTag.replace("#", "%"));

		// クラブ所属メンバーの詳細情報（所有キャラクター等）を取得
		List<PlayerInfoDto> playerInfoDtoList = brawlStarsAPIService.getClubMemberInfo(clubPlayerAPIDto.getItems());

		int insertSuccessCount = 0;		// DB更新件数

		// クラブ、プレイヤー、キャラクターのマスタを更新し、ユーザおよび所有キャラ関係、大会参加候補メンバーをDB登録
		for (PlayerInfoDto playerInfoDto : playerInfoDtoList) {
			insertSuccessCount += playerService.createOnDuplicateKeyUpdate(playerInfoDto)
									+ brawlerMasterService.create(playerInfoDto.getBrawlers())
									+ ownBrawlerService.create(playerInfoDto)
									+ championshipMemberService.create(playerInfoDto.getTag());
		}

		return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
	}

	// ログイン中ユーザが有するゲームアカウントのプレイヤータグをユーザ情報に紐づけて登録
	// 同時にプレイヤー情報、キャラクター情報をマスタ登録し、大会参加候補メンバーに自分も加える
	@PostMapping("/user/tag")
	ResponseEntity<Object> savePlayerTag(@Validated @RequestBody PlayerTagDto playerTagDto,
											BindingResult result) {

		// バリデーションエラー時は、エラー内容を返却
		if (result.hasErrors()) {
			return createErrorResponse(result);
		}

		// 公式APIからプレイヤー情報を取得するために、入力されたプレイヤータグの先頭#を%にする
		String playerTag = "%" + playerTagDto.getPlayerTag();

		// 公式APIからプレイヤー情報を取得
		PlayerInfoDto playerInfoDto = brawlStarsAPIService.getPlayerInfo(playerTag);

		// 公式APIからプレイヤー情報を取得できなかった場合
		if (playerInfoDto == null) {
			return createErrorResponse("playerTag", playerTag, "プレイヤーが見つかりませんでした。");
		}

		int registCount = 0;	// DB更新件数

		// クラブ、プレイヤー、キャラクターのマスタを更新し、ユーザおよび所有キャラ関係をDB登録
		registCount += clubService.create(playerInfoDto.getClub())
						+ playerService.createOnDuplicateKeyUpdate(playerInfoDto)
						+ userService.updatePlayerTag(playerInfoDto.getTag())
						+ brawlerMasterService.create(playerInfoDto.getBrawlers())
						+ ownBrawlerService.create(playerInfoDto)
						// 大会参加候補メンバーに自分を追加
						+ championshipMemberService.create(playerInfoDto.getTag());

		if (registCount > 0) {
			// プレイヤー情報、キャラクター情報はDB登録済みだった場合、更新件数は最低1件
			return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
		} else {
			// 大会参加候補メンバー登録すら失敗していた場合
			return createErrorResponse("playerTag", playerTag, "プレイヤータグ設定に失敗しました。再度お試しください。");
		}
	}

	// アカウント削除
	@PostMapping("/user/delete")
	ResponseEntity<Object> deleteUser() {

		if (userService.delete((Integer)session.getAttribute("userId")) > 0) {
			// 削除に成功した場合
			return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
		} else {
			// 削除できていなかった場合
			return createErrorResponse(null, null, "アカウントが削除できませんでした。再度お試しください。");
		}
	}

	// ユーザ自身のプレイヤータグを取得
	@GetMapping("/user/tag")
	UserEntity getMyPlayerTag() {

		return userService.search((Integer)session.getAttribute("userId"));
	}

	// キャラクターマスタの全レコードを取得
	@GetMapping("/brawler/all")
	List<BrawlerMasterEntity> getAllFromBrawlerMaster() {

		return brawlerMasterService.getAll();
	}

	// キャラクターマスタのタイプ（長距離、タンクなど）を更新
	@PostMapping("/brawler/update")
	ResponseEntity<Object> updateType(@RequestBody List<BrawlerMasterEntity> brawlerList) {

		// ユーザの管理者権限の有無を確認
		int admin = userService.search((Integer)session.getAttribute("userId")).getAdmin();

		if (admin == 1 && brawlerMasterService.updateType(brawlerList) > 0) {
			// 管理者権限を持ち、且つ更新に成功した場合
			return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
		} else {
			// 更新できていなかった場合
			return createErrorResponse(null, null, "キャラクターマスタが更新できませんでした。再度お試しください。");
		}

	}

	// エラー発生時のレスポンス内容を作成（バリデーションエラー以外）
	private ResponseEntity<Object> createErrorResponse(String keyName, Object keyValue, String message) {

		List<ErrorResponseDto> list = new ArrayList<ErrorResponseDto>();
		list.add(new ErrorResponseDto(keyName, keyValue, message));
		return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
	}

	// エラー発生時のレスポンス内容を作成（バリデーションエラー）
	private ResponseEntity<Object> createErrorResponse(BindingResult result) {

		List<ErrorResponseDto> list = new ArrayList<ErrorResponseDto>();
		for (FieldError e : result.getFieldErrors()) {
			list.add(new ErrorResponseDto(
										e.getField(),
										e.getRejectedValue(),
										e.getDefaultMessage()));
		}
		return new ResponseEntity<>(list, HttpStatus.BAD_REQUEST);
	}

}
