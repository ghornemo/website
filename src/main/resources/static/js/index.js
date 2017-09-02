/**
 * google authentication api
 */

function hideRows() {
	var president = $("input[type=radio]:checked").val();
	if(president === "trump") {
		$('#pinnedPosts tr:has(td:contains("JustinTrudeau"))').hide();
	} else if(president === "trudeau") {
		$('#pinnedPosts tr:has(td:contains("realDonaldTrump"))').hide();
	}
}

$(function(){
	$("#radiotrump").change(function() {
		$('#pinnedPosts tr:has(td:contains("realDonaldTrump"))').show();
		$('#pinnedPosts tr:has(td:contains("JustinTrudeau"))').hide();
		$("#searchBar").val("");
	});
	
	$("#radiotrudeau").change(function() {
		$('#pinnedPosts tr:has(td:contains("realDonaldTrump"))').hide();
		$('#pinnedPosts tr:has(td:contains("JustinTrudeau"))').show();
		$("#searchBar").val("");
	});
});
    
      function searchPosts() {
    	  // Declare variables 
    	  var input, filter, table, tr, td, i;
    	  input = document.getElementById("searchBar");
    	  filter = input.value.toUpperCase();
    	  table = document.getElementById("pinnedPosts");
    	  tr = table.getElementsByTagName("tr");
    	  

    	  // Loop through all table rows, and hide those who don't match the search query
    	  for (i = 0; i < tr.length; i++) {
    	    td = tr[i].getElementsByTagName("td")[0];
    	    if (td) {
    	      if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
    	        tr[i].style.display = "";
    	      } else {
    	        tr[i].style.display = "none";
    	      }
    	    } 
    	  }
    	  hideRows();
    	}
