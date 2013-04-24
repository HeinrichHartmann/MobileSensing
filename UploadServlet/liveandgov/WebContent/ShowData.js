
var markers;
var map;
var uuid;

function updateMap() {
  $.ajax({
    url: "JsonApi",
    type: 'GET',
    dataType: 'json',
    data: {uuid: this.uuid, sensorid: 'GPS', tsFrom: 0, tsTo: 2363771562598},
    success: function(data, status){
    	map.setView(data["data"][0], 15);
    	markers = L.polyline(data["data"]);
    	markers.addTo(map); 
    },
    error: function(a,c) {/* alert("ajax: " + c);*/}
  });
  $.ajax({
	    url: "JsonApi",
	    type: 'GET',
	    dataType: 'json',
	    data: {uuid: this.uuid, sensorid: 'Tags', tsFrom: 0, tsTo: 2363771562598},
	    success: function(data, status){
	    	for(var i = 0; i < data["data"].length; i++) {
	    		var date = new Date(data["data"][i]["ts"]);
		    	L.marker(data["data"][i]["latlon"]).addTo(map)
		        .bindPopup(data["data"][i]["tag"] + "<br/>" + date.toString());	
	    	}
	    	showOverview();
	    },
	    error: function(a,c) { /*alert("ajax: " + c);*/}
	  });
}


function initilize(uuid){
	this.uuid = uuid;
	map = L.map('map');

	L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
		maxZoom: 18,
		attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>'
	}).addTo(map);

  updateMap();  
}

function showOverview() {
	 $.ajax({
	    url: "JsonApi",
	    type: 'GET',
	    dataType: 'json',
	    data: {overview: 'yes'},
	    success: function(data, status){
	    	var table_obj = $("#overview").attr('border', '1');
	    	table_obj.append($('<th>count(*)</th><th>sensor</th><th>uuid</th><th>device id</th><th>smartphone</th><th>min time</th><th>max time</th>'));
		    $.each(data["data"], function(index, item){
	    		var mindate = new Date(item.min);
	    		var maxdate = new Date(item.max);
		         table_obj.append($('<tr><td>'
		        		 +item.count+'</td><td>'
		        		 +((item.sensorid==='GPS')?'<a href="ShowData?uuid='+item.uuid+'">GPS (show on map)</a>':item.sensorid)+'</td><td>'
		        		 +item.uuid+'</td><td>'
		        		 +item.textuuid+'</td><td>'
		        		 +item.model+'</td><td>'
		        		 +mindate.toString()+'</td><td>'
		        		 +maxdate.toString()+'</td></tr>'));
		    });

	    },
	    error: function(a,c) { /*alert("ajax: " + c);*/}
	  });
	 
}