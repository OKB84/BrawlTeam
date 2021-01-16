// ページネーション実装のためにライブラリを使用
// https://www.kabanoki.net/4320/
Vue.component('paginate', VuejsPaginate);

var app = new Vue({
	el: '#app',
	data: {
		playerList: [],				// 大会参加候補メンバー
		playerDetail: {},			// プレイヤー詳細情報
		chartLoaded: false,			// 詳細レーダーチャートの表示切り替え
		showModalDetail: false,		// プレイヤー詳細のモーダル表示切り替え
		showModalAdd: false,		// プレイヤー追加のモーダル表示切り替え
		parPage: 10,				// ページネーション設定（１ページの表示件数）
	    currentPage: 1,				// ページネーション設定（現在のページ）
		playerTag: '',				// メンバー追加欄のプレイヤータグ入力値
		playerTagError: '',			// メンバー追加時のエラーメッセージ
		importError: '',			// クラブメンバーインポート時のエラーメッセージ
		showUpdatingModal: false,	// API通信中のモーダルの表示切り替え
		modalUpdatingMessage: ''	// API通信中のモーダル中のメッセージ
	},
	/*
	ページロード時にメンバー一覧を取得
 	*/
	created: function() {
		this.getMemberList();
	},
	/*
	ページネーションに必要な計算を行う
 	*/
	computed: {
		// 現在のページに表示する大会情報を返す
		getPlayerList: function() {
			let current = this.currentPage * this.parPage;
			let start = current - this.parPage;
			let currentList = this.playerList.slice(start, current);
			const listLength = currentList.length;
			if (listLength < this.parPage) {
				for (let i = 0; i < this.parPage - listLength; i++) {
					currentList.push({});
				}
			}
			return currentList;
		},
		// 現在のページ数を返す
		getPageCount: function() {
			return Math.ceil(this.playerList.length / this.parPage);
		}
	},
	/*
	追加モーダルの非表示時にエラーメッセージと入力値をクリアする
 	*/
	watch: {
		showModalAdd: function() {
			if (!this.showModalAdd) {
				this.eraseErrorMessage();
				this.playerTag = '';
			}
		}
	},

	methods: {
		// メンバーを取得
		getMemberList: function() {
			fetch('/api/member/all')
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.playerList = data;
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},

		// ページネーションのページ切り替え
	    clickCallback: function (pageNum) {
	    	this.currentPage = Number(pageNum);
	    },

		// メンバーを削除
		deleteMember: function(playerTag) {

			if (!confirm("メンバーを削除しますか？")) {
				return;
			}

			// FetchAPIのオプション準備
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: JSON.stringify({
					playerTag: playerTag
				})
			};

			fetch('/api/member/delete', param)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					if (data.length === 0){		// 削除成功時
						this.showModalDetail = false;	// 詳細モーダルから削除した場合はモーダルを閉じる
						this.getMemberList();
					} else {
						alert('削除に失敗しました。画面をリロードしてください。');
					}
				})
				.catch(error => {	// エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// メンバーのプレイヤー詳細情報を表示（大会新規作成画面とソース重複状態）
		showDetail: function(playerTag) {
			this.playerDetail = null;
			this.chartLoaded = false;

			// パーセントはパラメータとして受け取れないため置換して送信
			fetch('/api/member/show/' + playerTag.replace('%', ''))
				.then(response => {
					this.getBattlelogList(playerTag);	// 3on3の勝率を表示
					this.chartLoaded = true;			// レーダーチャートを表示
					this.showModalDetail = true;		// プレイヤー詳細モーダルを表示
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.playerDetail = data;

					// 詳細表示モーダルからの削除時に必要になるプレイヤータグデータをセット
					// （戻り値にセットされていないため）
					this.playerDetail.playerTag = playerTag;
					this.displayChart();
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});

		},

		// レーダーチャート描画（大会新規作成画面とソース重複状態）
		displayChart: function() {

			// 描画用の平均トロフィー配列を作成
			const trophieAvgs = [
					this.playerDetail.avgTroAllBrawlers,
					this.playerDetail.avgTroLongRange,
					this.playerDetail.avgTroLongRangeSupHeavy,
					this.playerDetail.avgTroMidRange,
					this.playerDetail.avgTroMidRangeSupHeavy,
					this.playerDetail.avgTroHeavyWeight,
					this.playerDetail.avgTroSemiHeavyWeight,
					this.playerDetail.avgTroThrower
				]

			const maxTrophieAvg = Math.max(...trophieAvgs);		// 平均トロフィー最大値

			// 初期表示時以外は、チャートの最大値を平均トロフィ最大値の100未満繰り上げに設定
			if (maxTrophieAvg > 0) {
				this.chartMaxValue = Math.ceil(maxTrophieAvg / 100) * 100;
			}

			var ctx = document.getElementById("radarChart");
			var myChart = new Chart(ctx, {
			  //グラフの種類
			  type: 'radar',
			  //データの設定
			  data: {
			      //データ項目のラベル
			      labels: ["全キャラクター", "長距離", "長タンクメタ", "中距離", "中タンクメタ", "タンク", "セミタンク高機動", "スローワー"],
			      //データセット
			      datasets: [
			          {
			             //凡例のラベル
			              label: "平均最多トロフィー数",
			              //背景色
			              backgroundColor: "rgba(80,126,164,0.3)",
			              //枠線の色
			              borderColor: "rgba(80,126,164,1)",
			              //結合点の背景色
			              pointBackgroundColor: "rgba(80,126,164,1)",
			              //結合点の枠線の色
			              pointBorderColor: "#fff",
			              //結合点の背景色（ホバーしたとき）
			              pointHoverBackgroundColor: "#fff",
			              //結合点の枠線の色（ホバーしたとき）
			              pointHoverBorderColor: "rgba(80,126,164,1)",
			              //結合点より外でマウスホバーを認識する範囲（ピクセル単位）
			              hitRadius: 5,
			              //グラフのデータ
			              data: trophieAvgs
			          }
			      ]
			  },
			 options: {
			    // レスポンシブ指定
			    responsive: true,
			    scale: {
			      ticks: {
			        // 最小値の値を0指定
			        beginAtZero:true,
			        min: 0,
			        // 最大値を指定
			        max: this.chartMaxValue,
			      }
			    }
			  }
			});
		},

		// プレイヤータグでメンバー追加
		addMember: function() {

			// すべてのエラーメッセージをクリア
			this.eraseErrorMessage();

			// FetchAPIのオプション準備
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: JSON.stringify({
					playerTag: this.playerTag
				})
			};

			fetch('/api/member/create', param)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					if (data.length === 0) {	// 追加成功時
						this.getMemberList();				// 追加後のメンバーリストを取得し更新
						alert('メンバーが追加されました。');
						this.playerTag = '';				// 入力欄をクリア
						this.showModalAdd = false;			// モーダルを非表示
					} else {	// バリデーションエラー等発生の場合
						this.displayErrorMessage(data);
					}
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// クラブメンバーを一括メンバー追加
		importFromClub: function() {

			if (!confirm('所属クラブのメンバーを全員インポートしますがよろしいですか？')) {
				return;
			}

			// すべてのエラーメッセージをクリア
			this.eraseErrorMessage();

			this.modalUpdatingMessage = 'クラブメンバーのインポート中...';
			this.switchShowUpdatingModal();		// ローディングモーダルを表示

			// FetchAPIのオプション準備（GET通信で思わぬ呼ばれ方をしないようにPOSTで通信）
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: null
			};

			fetch('/api/member/import', param)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					if (data.length === 0) {	// 追加成功時

						this.modalUpdatingMessage = 'インポートが完了しました。';
						this.getMemberList();				// 追加後のメンバーリストを取得し更新

						// 更新完了メッセージを表示後にローディングモーダルを非表示
						setTimeout(this.switchShowUpdatingModal, 1000);

					} else {	// バリデーションエラー等発生の場合
						this.displayErrorMessage(data);
					}
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// バトルログリストを取得
		getBattlelogList: function(playerTag) {

			fetch('/api/member/battlelog/' + playerTag.replace('%', ''))
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					console.log(data)
					this.get3on3Result(data);
				})
				.catch(error => {	// その他エラーの場合
					alert('勝率の取得に失敗しました。時間を置いて再度お試しください。');
					console.log(error);
				});
		},

		// バトルログリストから3on3の勝率を算出
		get3on3Result: function(battlelogList) {

			let battle = 0;		// 3on3のバトル数
			let victory = 0;	// 3on3の勝利数

			// 3on3のモード
			const modeList = [
								"gemGrab",
								"brawlBall",
								"siege",
								"heist",
								"bounty",
								"hotZone",
								"presentPlunder"
							]

			// バトルログからバトル数及び勝利数を計算
			for (battlelog of battlelogList) {

				// モードリストに該当するモードの場合はバトル数を加算
				if (modeList.some(mode => battlelog.event.mode == mode)) {
					battle++;
					// 勝利していれば勝利数を加算
					if (battlelog.battle.result == "victory") {
						victory++;
					}
				}
			}

			// 勝率をプレイヤー詳細にセット
			this.$set(this.playerDetail, 'victoryRate', Math.round(victory * 100 / battle));
		},

		// すべてのエラーメッセージをクリア
		eraseErrorMessage: function() {
			this.playerTagError = '';
			this.importError = '';
		},


		// レスポンスのエラー内容を各フィールドに表示
		displayErrorMessage: function(data) {
			for (d of data) {
				switch (d.keyName) {
					case "playerTag":
						this.playerTagError = d.message;
						break;
					case "import":
						this.importError = d.message;
						break;
				}
			}
		},

		// API通信中に表示する更新中モーダルの表示切り替え
		switchShowUpdatingModal: function() {

			// 表示中の状態から非表示にする場合はメンバー追加モーダルを非表示にする
			if (this.showUpdatingModal) {
				this.showModalAdd = false;
			}

			this.showUpdatingModal = !this.showUpdatingModal;
		}
	}
})
