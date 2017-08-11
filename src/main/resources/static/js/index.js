/**
 * google authentication api
 */
$(function(){
	$("#radiotrump").change(function() {
		$('#pinnedPosts tr:has(td:contains("realDonaldTrump"))').show();
		$('#pinnedPosts tr:has(td:contains("JustinTrudeau"))').hide();

	});
	
	$("#radiotrudeau").change(function() {
		$('#pinnedPosts tr:has(td:contains("realDonaldTrump"))').hide();
		$('#pinnedPosts tr:has(td:contains("JustinTrudeau"))').show();

	});
});
    window.onbeforeunload = function(e){
      gapi.auth2.getAuthInstance().signOut();
    };

      function onSignIn(googleUser) {
        // Useful data for your client-side scripts:
        var profile = googleUser.getBasicProfile();
        console.log("ID: " + profile.getId()); // Don't send this directly to your server!
        console.log('Full Name: ' + profile.getName());
        $("#nameField").text(profile.getName());
        console.log('Given Name: ' + profile.getGivenName());
        console.log('Family Name: ' + profile.getFamilyName());
        console.log("Image URL: " + profile.getImageUrl());
        console.log("Email: " + profile.getEmail());

        // The ID token you need to pass to your backend:
        var id_token = googleUser.getAuthResponse().id_token;
        console.log("ID Token: " + id_token);
        
        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/authenticate');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
          console.log('Signed in as: ' + xhr.responseText);
        };
        xhr.send('idtoken=' + id_token);
      };
