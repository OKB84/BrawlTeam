var app = new Vue({
	el: '#app',
	data: {
		currentPlayerTag: '',		// 設定済みのプレイヤータグ
		playerTag: '',				// メンバー追加欄のプレイヤータグ入力値
		playerTagError: ''			// メンバー追加時のエラーメッセージ
	},
	// ページロード時に設定済みのプレイヤータグを取得
	created: function() {
		this.getCurrentPlayerTag();
	},

	methods: {

		// 設定済みのプレイヤータグを取得
		getCurrentPlayerTag: function() {
			fetch('/api/user/tag')
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					this.currentPlayerTag = data.playerTag ? data.playerTag.replace('%', '#') : null;
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// プレイヤータグをプロフィールに追加
		savePlayerTag: function() {

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

			fetch('/api/user/tag', param)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					if (data.length === 0) {	// 追加成功時
						this.getCurrentPlayerTag();			// プレイヤータグ表示欄を更新
						this.playerTag = '';				// 入力欄をクリア
					} else {	// バリデーションエラー等発生の場合
						this.playerTagError = data[0].message;
					}
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// アカウントを削除
		deleteAccount: function() {

			if (!confirm('アカウントを削除しますがよろしいですか？')) {
				return;
			}

			// すべてのエラーメッセージをクリア
			this.eraseErrorMessage();

			// FetchAPIのオプション準備（GET通信で思わぬ呼ばれ方をしないようにPOSTで通信）
			const param  = {
				method: "POST",
				headers: {
					"Content-Type": "application/json; charset=utf-8"
				},
				// リクエストボディ
				body: null
			};

			fetch('/api/user/delete', param)
				.then(response => {
					return response.json();		// Promiseを返す
				})
				.then(data => {		// JSONデータ
					if (data.length === 0) {	// 追加成功時
						alert('アカウントが削除されました。ご利用ありがとうございました。');
						window.location.href = '/';
					} else {
						alert(data[0].message);
					}
				})
				.catch(error => {	// その他エラーの場合
					alert('予期せぬエラーが発生しました。運営にお問い合わせください。');
					console.log(error);
				});
		},

		// すべてのエラーメッセージをクリア
		eraseErrorMessage: function() {
			this.playerTagError = '';
			this.importError = '';
		}
	}
})
