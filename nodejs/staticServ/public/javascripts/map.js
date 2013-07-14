(function () {
	var getLatLon = function (value) {
		return [value.lat, value.lon];
	};

	var sortByTimestamp = function (a, b) {
		return a.ts - b.ts;
	};

	function Map (id) {
		this._map = L.map(id);
		L.tileLayer('http://{s}.tile.cloudmade.com/BC9A493B41014CAABB98F0471D759707/997/256/{z}/{x}/{y}.png', {
          maxZoom: 18,
          attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>'
        }).addTo(this._map);
	};

	Map.prototype.reset = function(data) {
		this.clearMap();
		this._map.setView(getLatLon(data[0]), 13);
		this._data = data.sort(sortByTimestamp);
        this.createLatLongArray(100000);
        this.doPolylines();
	};

	Map.prototype.clearMap = function() {
		for(i in this._map._layers) {
			if(this._map._layers[i]._path != undefined) {
				try {
					this._map.removeLayer(this._map._layers[i]);
				}
				catch(e) {
					console.log("problem with " + e + this._map._layers[i]);
				}
			}
		}
	};

	Map.prototype.createLatLongArray = function(delta) {
		this._latLon = [];
		var currentArray = [];
		var lastTimeStamp = this._data[0].ts;
		var curr = null;
		for (var i = 0; i < this._data.length; i++) {
			curr = this._data[i];
			if(curr.ts - lastTimeStamp > delta) {
				this._latLon.push(currentArray);
				currentArray = [];
			}
			currentArray.push(getLatLon(curr));
			lastTimeStamp = curr.ts;
		};
		this._latLon.push(currentArray);
	};

	Map.prototype.doPolylines = function() {
		for (var i = this._latLon.length - 1; i >= 0; i--) {
			var randColor = getRandomColor();
			L.polyline(this._latLon[i], {color: randColor}).addTo(this._map);
		};
	};

	window.Map = Map;

	var getRandomColor = function () {
		var letters = '0123456789ABCDEF'.split('');
		var color = '#';
		for (var i = 0; i < 6; i++) {
			color += letters[Math.round(Math.random() * 15)];
		};
		return color;
	};
})();