/**
 *
 */

function onSignIn(googleUser) {
	var id_token = googleUser.getAuthResponse().id_token;

	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/google/signin');
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xhr.onload = function() {
    	if (xhr.readyState === 4) {
  	    	if (xhr.status === 200) {
	        	window.location.href = '/championship/index';
	        	var auth2 = gapi.auth2.getAuthInstance();
	            auth2.disconnect();
  	    	} else {
  	    		alert('Googleアカウント認証に失敗しました。');
  	    	}
  	  	}
	};
	xhr.send('idTokenString=' + id_token);
}