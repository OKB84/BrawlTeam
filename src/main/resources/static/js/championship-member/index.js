// ページネーション実装のためにライブラリを使用
// https://www.kabanoki.net/4320/
Vue.component('paginate', VuejsPaginate);

var app = new Vue({
	el: '#app',
	data: {
		playerList: [],				// 大会参加候補全メンバー
		playerDetail: {},			// プレイヤー詳細情報
		chartLoaded: false,			// 詳細レーダーチャートの表示切り替え
		doughnutChartLoaded: false,		// ドーナツチャートの表示切り替え
		showModalDetail: false,		// プレイヤー詳細のモーダル表示切り替え
		showModalAdd: false,		// プレイヤー追加のモーダル表示切り替え
		parPage: 10,				// ページネーション設定（１ページの表示件数）
	    currentPage: 1,				// ページネーション設定（現在のページ）
		playerTag: '',				// メンバー追加欄のプレイヤータグ入力値
		playerTagError: '',			// メンバー追加時のエラーメッセージ
		importError: '',			// クラブメンバーインポート時のエラーメッセージ
		showUpdatingModal: false,	// API通信中のモーダルの表示切り替え
		modalUpdatingMessage: '',	// API通信中のモーダル中のメッセージ
		searchWord: '',				// 検索ワード
		sortKey: 'trophies',		// ソート対象の列名
		sortAscFlag: false			// ソートの昇順フラグ
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
			const current = this.currentPage * this.parPage;
			const start = current - this.parPage;

			// ソート順を考慮
			const sortReverse = this.sortAscFlag ? 1 : -1;

			let currentList = this.playerList
									// 検索ワードによるフィルター
									// ページロード時にplayerListが空の状態からスタートするためplayer.nameによる条件分岐付き
									.filter(player => player.name ?
											player.name.toLowerCase().indexOf(this.searchWord) > -1
											: true)
									// 昇順・降順で並び替え
									.sort((a, b) => (a[this.sortKey] - b[this.sortKey]) * sortReverse)
									.slice(start, current);

			const listLength = currentList.length;

			// データがない行は空行で埋める
			if (listLength < this.parPage) {
				for (let i = 0; i < this.parPage - listLength; i++) {
					currentList.push({});
				}
			}
			return currentList;
		},
		// 総ページ数を返す
		getPageCount: function() {

			// 検索ワードによるフィルター（ページロード時にplayerListが空の状態からスタートするためplayer.nameによる条件分岐付き）
			return Math.ceil(this.playerList
									// 検索ワードによるフィルター
									// ページロード時にplayerListが空の状態からスタートするためplayer.nameによる条件分岐付き
									.filter(player => player.name ?
											player.name.toLowerCase().indexOf(this.searchWord) > -1
											: true)
									.length / this.parPage);
		}
	},
	watch: {
		// 追加モーダルの非表示時にエラーメッセージと入力値をクリアする
		showModalAdd: function() {
			if (!this.showModalAdd) {
				this.eraseErrorMessage();
				this.playerTag = '';
			}
		},
		// 検索結果はページネーションの先頭から表示する
		searchWord: function() {
			this.currentPage = 1;
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
			this.doughnutChartLoaded = false;

			// パーセントはパラメータとして受け取れないため置換して送信
			fetch('/api/member/show/' + playerTag.replace('%', ''))
				.then(response => {
					this.chartLoaded = true;			// レーダーチャートを表示
					this.doughnutChartLoaded = true;	// ドーナツチャートを表示
					this.showModalDetail = true;		// プレイヤー詳細モーダルを表示
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.playerDetail = data;

					// 詳細表示モーダルからの削除時に必要になるプレイヤータグデータをセット
					// （戻り値にセットされていないため）
					this.playerDetail.playerTag = playerTag;
					this.displayChart();
					this.displayDoughnutChart();
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});

		},

		// テーブル表示並び替え
		sortBy: function(key) {

			// 同じ列名をクリックした場合のみ昇順・降順が切り替わる
			this.sortKey === key
				? (this.sortAscFlag = !this.sortAscFlag)
				: (this.sortAscFlag = false);

			this.sortKey = key;		// ソートする列名をセット
		},

		// レーダーチャート描画（大会新規作成、編集画面とソース重複状態）
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

			// 描画用の中央値トロフィー配列を作成
			const trophieMedians = [
					this.playerDetail.medianTroAllBrawlers,
					this.playerDetail.medianTroLongRange,
					this.playerDetail.medianTroLongRangeSupHeavy,
					this.playerDetail.medianTroMidRange,
					this.playerDetail.medianTroMidRangeSupHeavy,
					this.playerDetail.medianTroHeavyWeight,
					this.playerDetail.medianTroSemiHeavyWeight,
					this.playerDetail.medianTroThrower
				]

			const maxTrophieMedian = Math.max(...trophieMedians);		// 中央値トロフィー最大値

			const greaterValue = maxTrophieAvg >= maxTrophieMedian ? maxTrophieAvg : maxTrophieMedian;

			// チャートの最大値を平均トロフィ最大値の100未満繰り上げに設定
			this.chartMaxValue = Math.ceil(greaterValue / 100) * 100;

			var ctx = document.getElementById("radarChart");
			var myChart = new Chart(ctx, {
			  //グラフの種類
			  type: 'radar',
			  //データの設定
			  data: {
			      //データ項目のラベル
			      labels: ["全キャラクター", "長距離", "長タンクメタ", "中距離", "中タンクメタ", "タンク", "セミタンク", "スローワー"],
			      //データセット
			      datasets: [
			          {
			              //凡例のラベル
			              label: "平均値",
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
			              hitRadius: 10,
			              //グラフのデータ
			              data: trophieAvgs
			          },
			          {
						  //凡例のラベル
			              label: "中央値",
				          //背景色
				          backgroundColor: "rgba(200,112,126,0.3)",
				          //枠線の色
				          borderColor: "rgba(200,112,126,1)",
				          //結合点の背景色
				          pointBackgroundColor: "rgba(200,112,126,1)",
				          //結合点の枠線の色
				          pointBorderColor: "#fff",
				          //結合点の背景色（ホバーしたとき）
				          pointHoverBackgroundColor: "#fff",
				          //結合点の枠線の色（ホバーしたとき）
				          pointHoverBorderColor: "rgba(200,112,126,1)",
			              //結合点より外でマウスホバーを認識する範囲（ピクセル単位）
			              hitRadius: 10,
			              //グラフのデータ
			              data: trophieMedians
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
			    },
				legend: {
					position: 'right',	// 凡例表示位置
					labels: {
						fontSize: 8		// 凡例フォントサイズ
					}
				},
				title: {
					display: true,
					//グラフタイトル
					text: '最多トロフィー数'
				}
			  }
			});
		},

		// プレイヤータグの入力制限
		inputHalfSizeRestriction: function() {
            tmp_value = this.playerTag;
            if(tmp_value){
				// 小文字は大文字にした上で、半角英数字以外は入力値から自動で消去
                this.playerTag = tmp_value.toUpperCase().replace(/[^0-9A-Z]/g,'');
            }
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

		// ドーナツチャート描画（大会新規作成、編集画面とソース重複状態）
		displayDoughnutChart: function() {

			// 描画用の平均トロフィー配列を作成
			const chartData = [
					this.playerDetail.useLongRange,
					this.playerDetail.useLongRangeSupHeavy,
					this.playerDetail.useMidRange,
					this.playerDetail.useMidRangeSupHeavy,
					this.playerDetail.useHeavyWeight,
					this.playerDetail.useSemiHeavyWeight,
					this.playerDetail.useThrower
				]

			var ctx = document.getElementById("doughnutChart");
			var myDoughnutChart= new Chart(ctx, {
				type: 'doughnut',
				data: {
			    	labels: ["長距離", "長タンクメタ", "中距離", "中タンクメタ", "タンク", "セミタンク", "スローワー"],
					datasets: [{
						backgroundColor: [
							"#960200",
							"#CE6C47",
							"#344055",
							"#84A9C0",
							"#E8871E",
							"#EDB458",
							"#D4D4AA"
						],
						data: chartData //グラフのデータ
					}]
				},
				options: {
			    	responsive: true,	// レスポンシブ指定
					legend: {
						position: 'right',	// 凡例表示位置
						labels: {
							fontSize: 8,		// 凡例フォントサイズ
							padding: 7,			// 凡例行間
							filter: function(item, chartData) {
								// 使用回数が0回のキャラタイプは凡例を表示しない
								return chartData.datasets[0].data[item.index] > 0;
							}
						}
					},
					title: {
						display: true,
						//グラフタイトル
						text: 'キャラ別使用回数（直近25試合）'
					}
				}
			});
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
