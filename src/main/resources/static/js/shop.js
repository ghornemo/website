/**
 * The Javascript / JQuery file for the store.
 */

/**
 * google authentication api
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
        };
        xhr.send('idtoken=' + id_token);
      };

      function initMap() {
        var myLatlng = {lat: 45.306505, lng:-66.084697};

        var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 13,
          center: myLatlng
        });

        var info = "Store Pickup Location";
        
        var marker = new google.maps.Marker({
          position: myLatlng,
          map: map,
          title: 'Shop Location'
        });
        
        marker.infowindow = new google.maps.InfoWindow({
            content: info,
            disableAutoPan: true
        });

        //marker.infowindow.open(map, marker);
       

      }

      
    //Executes when document is ready.
      $(function () {
    	var name = $('#nameField').text(); 
      
    	  if(name === 'Not signed in') {
    	  }
          initMap(); //initialize map.

      });