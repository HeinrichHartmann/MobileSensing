
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
    error: function(a,c) { alert("ajax: " + c);}
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
