// ページネーション実装のためにライブラリを使用
// https://www.kabanoki.net/4320/
Vue.component('paginate', VuejsPaginate);

var app = new Vue({
	el: '#app',
	data: {
		championshipId: '',			// 編集対象の大会ID
		playerList: [],				// 大会参加候補プレイヤー
		teamList: [],				// 大会参加チーム
		maxNumOfTeam: 0,			// 全員大会参加可能最大チーム数
		date: '',					// 開催日付
		time: '',					// 開催時刻
		name: '',					// 大会名
		invalidDateError: '',		// エラーメッセージ（過去日付）
		nameError: '',				// エラーメッセージ（名前フォーマット）
		dateError: '',				// エラーメッセージ（日付フォーマット）
		timeError: '',				// エラーメッセージ（時刻フォーマット）
		teamListError: '',			// エラーメッセージ（チーム情報不足）
		parPage: 15,				// ページネーション設定（１ページの表示件数）
	    currentPage: 1,				// ページネーション設定（現在のページ）
		playerDetail: {},			// プレイヤー詳細情報
		chartLoaded: true,			// レーダーチャートの表示切り替え
		chartMaxValue: 100,			// レーダーチャートの最大値
		showModal: false,			// API通信中のモーダル表示切り替え
		modalMessage: ''			// API通信中のモーダル中のメッセージ
	},
	computed: {
		// 参加可能プレイヤー数算出
		canParticipatePlayerNum: function() {

			return this.playerList.reduce((sum, player) => {
				// 不参加のプレイヤーはカウントしない
				return player.teamNum === "0" ? sum : ++sum;
			}, 0);
		},
		// 現在のページに表示するメンバー情報を返す
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
	watch: {
		// 参加可能プレイヤー数を監視して、チーム数を管理
		canParticipatePlayerNum: function() {

			this.maxNumOfTeam = Math.ceil(this.canParticipatePlayerNum / 3);

			// 現在のチーム数が最大チーム数に満たない場合はチームを増やす
			if (this.maxNumOfTeam > this.teamList.length) {

				// 追加すべきチームのチーム番号は、歯抜けになっている番号があればその番号にする
				for (let i = 1; i <= this.maxNumOfTeam; i++) {
					let isExisted = false;
					for (let team of this.teamList) {
						if (isExisted) {
							continue;
						}
						if (team.teamNum === i) {
							isExisted = true;
							continue;
						}
					}
					// チーム番号順になるように配列に追加
					if (!isExisted) {
						this.teamList.splice(i - 1, 0, {
							'teamNum' : i,
							'name' : '',
							'member' : [],
							'trophies' : 0,
							'numOfBrawler' : 0
						});
					}
				}

			// 不参加のプレイヤーが増えて最大チーム数が減った場合
			} else if (this.maxNumOfTeam < this.teamList.length) {

				// チーム名未設定且つ所属プレイヤー無し且つチーム番号が最大チーム数をオーバーしているチームを削除
				for(p of this.playerList) {
					if (p.teamNum > this.maxNumOfTeam || this.teamList[this.maxNumOfTeam].name) {
						return;
					}
				}

				// チーム番号の大きい順にチームを削除
				this.teamList.splice(this.maxNumOfTeam, this.teamList.length - this.maxNumOfTeam);
			}
		}
	},

	/*
	ページロード時に大会参加候補プレイヤー一覧および編集対象の大会情報を取得
 	*/
	created: function() {

		this.championshipId = document.getElementById("championshipId").value;

		let tempPlayerList = null;

		fetch('/api/member/all')
			.then(response => {
				return response.json();		// Promiseを返す
			})
			.then(data => {		// JSONデータ

				tempPlayerList = data;

				// プレイヤー詳細領域の初期表示
				this.playerDetail = {
					name: '-',
					trophies: '-',
					highestTrophies: '-',
					powerPlayPoints: '-',
					highestPowerPlayPoints: '-',
					soloVictories: '-',
					duoVictories: '-',
					avgTroAllBrawlers: 0,
					avgTroLongRange: 0,
					avgTroLongRangeSupHeavy: 0,
					avgTroMidRange: 0,
					avgTroMidRangeSupHeavy: 0,
					avgTroHeavyWeight: 0,
					avgTroSemiHeavyWeight: 0,
					avgTroThrower: 0
				}
				this.displayChart();

			fetch('/api/championship/show/' + this.championshipId)
				.then(response => {
					return response.json();
				})
				.then(data => {
					// 基本情報をフォームに受け渡し
					const convertedDate = new Date(data.date);
					const year = convertedDate.getFullYear();
					const month = ("0"+(convertedDate.getMonth() + 1)).slice(-2);	// 0埋めを行う
					const date = ("0"+(convertedDate.getDate())).slice(-2);			// 0埋めを行う
					const hour = ("0"+(convertedDate.getHours())).slice(-2);		// 0埋めを行う
					const minute = ("0"+(convertedDate.getMinutes())).slice(-2);	// 0埋めを行う

					this.date = year + '-' + month + '-' + date;
					this.time = hour + ':' + minute;
					this.name = data.championshipName;

					const tempMembers = [];

					// 取得したチームリストにメンバーから削除されているプレイヤーがいれば、そのチームメンバーを一時的に復活
					data.teamList.forEach(team => {
						const notFoundMembers = team.memberList.filter(member => {
							return !tempPlayerList.some(player => {
								if (player.playerTag === member.playerTag) {
									player.teamNum = team.teamNum;
									return true;
								} else {
									return false;
								}
							});
						})
						tempMembers.push(...notFoundMembers);
					})
					tempPlayerList.push(...tempMembers)

					data.teamList.forEach(team => {

						let sumOfTrophies = 0;
						let sumOfNumOfBrawler = 0;

						team.memberList.forEach(member =>{
							sumOfTrophies += member.trophies;
							sumOfNumOfBrawler += member.numOfBrawler;
						})

						this.teamList.push({
							'teamNum' : team.teamNum,
							'name' : team.teamName,
							'member' : team.memberList,
							'trophies' : sumOfTrophies,
							'numOfBrawler' : sumOfNumOfBrawler
						})
					})

					this.playerList = tempPlayerList;
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});

			})
			.catch(error => {	// エラーの場合
				console.log(error);
			});
	},

	methods: {

		// 参加チームのプルダウン変更時に、チーム詳細テーブルを描画
		teamSelected: function(player) {

			// チーム変更となったプレイヤーが所属していたチームがあればメンバーから削除
			this.teamList.forEach(team => {
				team.member = team.member.filter(m => {
					if(m.playerTag === player.playerTag){
						// トロフィー数とキャラクター数をマイナス
						team.trophies -= player.trophies;
						team.numOfBrawler -= player.numOfBrawler;
						return false;
					} else {
						return true;
					}
				});
			});

			// いずれかのチームが選択された場合
			if (player.teamNum > 0) {

				const teamIndex = player.teamNum - 1;
				const memberSize = this.teamList[teamIndex].member.length;

				// 変更先のチームのメンバー数が3人の場合は、変更されたプルダウンを無選択状態にする
				if (memberSize === 3) {
					alert('チーム' + player.teamNum + 'は、すでに3人選択されています。');
					player.teamNum = null;
					return;
				}

				// 変更後のチームにプレイヤーを追加
				this.teamList[teamIndex].member.push(player);
				this.teamList[teamIndex].trophies += player.trophies;
				this.teamList[teamIndex].numOfBrawler += player.numOfBrawler;

			}
		},

		// デフォルトデータでチームリストを初期化
		initializeTeamList: function(numOfTeam) {

			this.teamList = [];

			for (let i = 1; i <= numOfTeam; i++) {
				this.teamList.push({
					'teamNum' : i,
					'name' : '',
					'member' : [],
					'trophies' : 0,
					'numOfBrawler' : 0
				});
			}
		},

		// 公式APIにアクセスして最新のプレイヤーステータスに更新
		updateToLatestPlayerInfo: function() {

			if (!confirm('チーム情報が初期化されますがよろしいですか？')) {
				return;
			}

			this.modalMessage = 'プレイヤー情報更新中...';
			this.switchShowModal();		// ローディングモーダルを表示

			fetch('/api/member/update')
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					// playerList内のplayerのteamNumプロパティが初期化されてしまう
					this.playerList = data;

					this.modalMessage = '更新が完了しました。';

					// teamNum初期化に伴ってチームリストも初期化が必要
					this.initializeTeamList(this.maxNumOfTeam);

					// 更新完了メッセージを表示後にローディングモーダルを非表示
					setTimeout(this.switchShowModal, 1000);
				})
				.catch(error => {	// エラーの場合
					// アカウントを削除しているプレイヤーがいた場合はどうするのか要検討！！
					console.log(error);
				});
		},

		// モーダル表示切り替え
		switchShowModal: function() {
			this.showModal = !this.showModal;
		},

		// POSTリクエスト送信で大会情報を登録（エラーレスポンスの場合はエラーメッセージ表示）
		submit: function() {

			this.eraseErrorMessage();	// エラーメッセージをクリア

			// サーバへ送りたいデータ
			const data = {
				/*
					バリデーションエラー表示箇所は配列のインデックスになるため、
					メンバーが空のチームもリストに入れたまま送信
				*/
				championshipId: this.championshipId,
				teamList: this.teamList,
				date: this.date,
				time: this.time,
				name: this.name
			};

			// FetchAPIのオプション準備
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: JSON.stringify(data)
			};

			// POSTリクエスト送信
			fetch('/api/championship/update', param)
				.then(response => {
					if(response.ok){
						alert('更新が完了しました。大会一覧画面へリダイレクトします。');
						window.location.href = '/championship/index';
					} else {
						return response.json();		// Promiseを返す
					}
				})
				.then(data => {		// JSONデータ
					this.displayErrorMessage(data);
					alert('エラーが発生しました。入力項目をご確認ください。')
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},

		// すべてのエラーメッセージをクリア
		eraseErrorMessage: function() {
			this.invalidDateError = '';
			this.nameError = '';
			this.dateError = '';
			this.timeError = '';
			this.teamListError = '';
			// リアクティブにするために代入演算子ではなくsetを使用
			this.teamList.forEach(team => this.$set(team, "message", ''));
		},

		// レスポンスのエラー内容を各フィールドに表示
		displayErrorMessage: function(data) {
			for (d of data) {
				switch (d.keyName) {
					case "validDate":
						this.invalidDateError = d.message;
						break;
					case "name":
						this.nameError = d.message;
						break;
					case "date":
						this.dateError = d.message;
						break;
					case "time":
						this.timeError = d.message;
						break;
					case "notMemberEmpty":
						this.teamListError = d.message;
						break;
					default:
						// チーム名がバリデーションエラーとなっている場合チーム配列のインデックスを抽出
						const regExp = /\[([0-9]+)\]/;
						const matches = regExp.exec(d.keyName);

						// リアクティブにするために代入演算子ではなくsetを使用
						this.$set(this.teamList[Number(matches[1])], "message", d.message);
				}
			}

			// 過去日時エラーメッセージは日付と時間の両方を入力済みの場合のみ表示
			if (this.dateError || this.timeError) {
				this.invalidDateError = '';
			}
		},
		// ページネーションのページ切り替え
	    clickCallback: function (pageNum) {
	    	this.currentPage = Number(pageNum);
	    },

		// メンバーのプレイヤー詳細情報を表示（メンバー一覧画面とソース重複状態）
		showDetail: function(playerTag) {

			// 空行クリック時は何もしない
			if (!playerTag) {
				return;
			}

			this.playerDetail = null;
			this.chartLoaded = false;


			// パーセントはパラメータとして受け取れないため置換して送信
			fetch('/api/member/show/' + playerTag.replace('%', ''))
				.then(response => {
					this.chartLoaded = true;
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.playerDetail = data;
					this.displayChart();
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});

		},
		// レーダーチャート描画（メンバー一覧画面とソース重複状態）
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
		}
	}

})