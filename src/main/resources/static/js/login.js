/**
 * 
 */

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
          location.reload();
        };
        xhr.send('idtoken=' + id_token);
        
      };
      
      function setCookie(cname, cvalue, exdays) {
    	    var d = new Date();
    	    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    	    var expires = "expires="+d.toUTCString();
    	    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    	}

    	function getCookie(cname) {
    	    var name = cname + "=";
    	    var ca = document.cookie.split(';');
    	    for(var i = 0; i < ca.length; i++) {
    	        var c = ca[i];
    	        while (c.charAt(0) == ' ') {
    	            c = c.substring(1);
    	        }
    	        if (c.indexOf(name) == 0) {
    	            return c.substring(name.length, c.length);
    	        }
    	    }
    	    return "";
    	}
    	
    	
    	//All stuff to do with remember me checkbox
    	$(function() {
    	      $( "#loginForm" ).submit(function( event ) {
    	    	  	if($("#rememberMe").is(':checked')) {
    	    		  var email = $('#email').val();
    	    		  setCookie('storedEmail', email, 365);//Remember email for 1 year.
    	    	  	} else {
    	    	  		setCookie('storedEmail', email, -1);//Expired cookie.
    	    	  	}
    	      });
    		var storedEmail = getCookie('storedEmail');
    		if(storedEmail !== '') {
    			$('#rememberMe').attr('checked','checked')
    			$('#email').val(storedEmail);
    		}
    	});