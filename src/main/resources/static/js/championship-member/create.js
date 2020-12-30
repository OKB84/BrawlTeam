var app = new Vue({
	el: '#app',
	data: {
		isImportActive: true,			// インポートボタンの活性制御
		playerTag: '',				// メンバー追加欄のプレイヤータグ入力値
		playerTagError: '',			// メンバー追加時のエラーメッセージ
		importError: '',			// クラブメンバーインポート時のエラーメッセージ
		showUpdatingModal: false,	// API通信中のモーダルの表示切り替え
		modalUpdatingMessage: ''	// API通信中のモーダル中のメッセージ
	},

	// ページロード時にログイン中ユーザのプレイヤータグを取得（インポートボタンの活性制御）
	created: function() {
		this.getUserPlayerTag();
	},

	methods: {

		// ユーザのプレイヤータグを取得しインポートボタンの活性制御
		getUserPlayerTag: function() {
			fetch('/api/user/tag')
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					// プレイヤータグが登録されていなければインポートボタンを非活性にする
					this.isImportActive = data.playerTag ? true : false;
				})
				.catch(error => {	// その他エラーの場合
					console.log(error);
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
						if(!confirm('メンバーが追加されました。続けて追加を行いますか？')){
							window.location.href = '/championship-member/index';
						}
						this.playerTag = '';				// 入力欄をクリア
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
					} else {	// バリデーションエラー等発生の場合
						this.displayErrorMessage(data);
						this.modalUpdatingMessage = 'インポートに失敗しました。';
					}
					// 更新完了メッセージを表示後にローディングモーダルを非表示
					setTimeout(this.switchShowUpdatingModal, 1000);
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					this.switchShowUpdatingModal();
					console.log(error);
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

			this.showUpdatingModal = !this.showUpdatingModal;
		}
	}
})
