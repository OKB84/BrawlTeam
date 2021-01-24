// ページネーション実装のためにライブラリを使用
// https://www.kabanoki.net/4320/
Vue.component('paginate', VuejsPaginate);

var app = new Vue({
	el: '#app',
	data: {
		championshipList: [],			// 作成済みの大会一覧
		championshipDetail: [],			// 大会詳細情報
		parPage: 10,					// ページネーション設定（１ページの表示件数）
	    currentPage: 1,					// ページネーション設定（現在のページ）
		showModal: false
	},
	/*
	ページロード時に作成済チャンピオンシップ情報を取得
 	*/
	created: function() {
		this.getAllChampionship();
	},
	/*
	ページネーションに必要な計算を行う
 	*/
	computed: {
		// 現在のページに表示する大会情報を返す
		getChampionshipList: function() {
			let current = this.currentPage * this.parPage;
			let start = current - this.parPage;
			let currentList = this.championshipList.slice(start, current);
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
			return Math.ceil(this.championshipList.length / this.parPage);
		}
	},
	methods: {
		// ページネーションのページ切り替え
	    clickCallback: function (pageNum) {
	    	this.currentPage = Number(pageNum);
	    },

		// 大会詳細表示
		show: function(championshipId) {

			// 空行クリック時は何もアクションしない
			if (!championshipId) {
				return;
			}

			// 大会IDを元に通信し詳細情報を取得
			fetch('/api/championship/show/' + championshipId)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.championshipDetail = data;
					this.showModal = true;		// 大会詳細のモーダルを表示
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},

		// 大会詳細ダウンロード
		download: function(championshipId) {

			// 空行クリック時は何もアクションしない
			if (!championshipId) {
				return;
			}

			// 大会IDを元に通信し詳細情報を取得
			fetch('/api/championship/download/' + championshipId)
				.then(response => {
					return response.blob();		// Promiseを返す
				})
				.then(blob => {		// JSONデータ
					var file = window.URL.createObjectURL(blob);
					window.location.assign(file);
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},

		// 大会削除
		deleteChampionship: function(championshipId) {

			let deleteChampionshipList = [];

			if (championshipId != null) {
				deleteChampionshipList.push({id: Number(championshipId)});
				if (!confirm("この大会を削除しますか？")) {
					return;
				}
			} else {
				deleteChampionshipList = this.championshipList.filter(c => c.checked === true);
				if (deleteChampionshipList.length === 0) {
					alert("削除する大会を選択してください。");
					return;
				}

				if (!confirm("選択中の大会を削除しますか？")) {
					return;
				}
			}



			// FetchAPIのオプション準備
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: JSON.stringify(deleteChampionshipList)
			};

			console.log(param.body);

			fetch('/api/championship/delete', param)
				.then(response => {
					if(response.ok){
						this.getAllChampionship();
					} else {
						return response.json();		// Promiseを返す
					}
				})
				.then(data => {		// JSONデータ
					this.showModal = false;
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},
		// 作成済みの大会一覧を取得
		getAllChampionship: function() {
			fetch('/api/championship/all')
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.championshipList = data;
				})
				.catch(error => {	// エラーの場合
					console.log(error);
				});
		},
		// 編集画面へ移動
		moveToEdit: function(championshipId) {
			window.location.href='/championship/edit/' + championshipId
		}
	}
})