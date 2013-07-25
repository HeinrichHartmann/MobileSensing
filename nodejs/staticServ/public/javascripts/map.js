(function () {
	//var apiUrl = "http://mobile-sensing.west.uni-koblenz.de:8888"
	var apiUrl = "http://localhost:8888"
	var getLatLon = function (value) {
		return new L.LatLng(value.lat, value.lon);
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
        // Register Routes on change
		$('.routes').change(this.showRoute());
		this._markers = [];
	};

	Map.prototype.reset = function(data) {
		this.clearMap();
		if(data.length === 0) {
    	// Get the last 6 Month
    	var start = new Date(new Date().getTime() - 60*60*24*30*6*1000).getTime();
    	// End is now
    	var end = new Date().getTime();
    	// We force a limit here to a default 10000
    	window.updateSensors(start, end, 10000);
    	return;
    }
		this._map.setView(getLatLon(data[0]), 13);
		this._data = data.sort(sortByTimestamp);
    this.createRoutes(100000);
    this.clearRouteDropdown();
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

	Map.prototype.createRoutes = function(delta) {
		this._routes = [];
		var numRoutes = 0;
		var currentRoute = new Route(0);
		var lastTimeStamp = [];
		var curr = null;
		for (var i = 0; i < this._data.length; i++) {
			curr = this._data[i];
			if(curr.ts - lastTimeStamp > delta) {
				this._routes.push(currentRoute);
				currentRoute = new Route(++numRoutes);
			}
			currentRoute.addPoint(curr);
			lastTimeStamp = curr.ts;
		};
		this._routes.push(currentRoute);
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
		for (var i = this._routes.length - 1; i >= 0; i--) {
			this._routes[i].draw(this._map);
		};
	};

	Map.prototype.clearRouteDropdown = function() {
		$('.routes')		
			.find('option')
			.remove()
			.end()
			.append('<option value="-1">All</option>');
	};

	Map.prototype.showRoute = function() {
		var self = this;
		return function() {
			var num = parseInt($('.routes').val());
			if(num === -1) {
				// Show all
				self.clearMap();
	      self.doPolylines();
			} else {
				self.clearMap();
				self._routes[num].draw(self._map);
				self._routes[num].focus(self._map);
				self._routes[num].syncTime();
			}
		};
	};

	Map.prototype.showMarker = function(ts) {
		var self = this;
		var uuid = this._data[0].uuid;
		var variance = 10000;
		$.ajax({
			url: apiUrl + '/gps/' + uuid + '/nearestTo/' + ts + '/variance/' + variance
		})
		.done(function (data) {
			if(data && data.length && data.length !== 0) {
				self._markers.push(L.marker([data[0].lat, data[0].lon]).addTo(self._map));
			}
		});
	};

	Map.prototype.removeMarker = function() {
		for (var i = this._markers.length - 1; i >= 0; i--) {
			this._map.removeLayer(this._markers[i]);
			this._markers.pop();
		};
	};

	window.Map = Map;

	var getRandomColor = function () {
		var letters = '0123456789ABCDEF'.split('');
		var color = '#';
		for (var i = 0; i < 6; i++) {
			color += letters[Math.round(Math.random() * 15)];
		};
		return "blue";
		return color;

	};

	// =====================
	// Route
	// =====================
	function Route (number) {
		this._number = number;
		this._points = [];
		this._latLonPoints = [];
	}

	Route.prototype.addPoint = function(point) {
		this._points.push(point);
		var latlon = getLatLon(point);
		this._latLonPoints.push(latlon);
	};

	Route.prototype.createPolyline = function(map) {
		var randColor = getRandomColor();
		this._polyline = L.polyline(this._latLonPoints, {color: randColor}).addTo(map);
	};

	Route.prototype.draw = function(map) {
		this.createPolyline(map);
		this.addTag(100000);
	};

	Route.prototype.clear = function(map) {
		map.removeLayer(this._polyline);
	};

	Route.prototype.focus = function(map) {
		map.setView(this._latLonPoints[0], 13);
	};

	Route.prototype.addTag = function(variance) {
		if(this.name !== undefined) {
			this._polyline.bindPopup("<p>" + this.name + "</p>");
			return;
		}
		var self = this;
		if(!variance) variance = 1000;
		var start = this._points[0];
		if(start === undefined)
			return;
		$.ajax({
			url: apiUrl + '/tags/' + start.uuid + '/nearestTo/' + start.ts + '/variance/' + variance
		})
		.done(function (data) {
			if(data && data.length && data.length !== 0) {
				self.name = data[0].txt;
				
			} else {
				self.name = "No Tag";
				self._polyline.bindPopup("<p> No Tag </p>");
			}
			$('.routes').append('<option value="' + self._number + '">' + self.name + ' ('+ self._points.length +')</option>');
		});
	};

	Route.prototype.syncTime = function() {
		var start = this._points[0].ts;
		var end = this._points[this._points.length - 1].ts;
		window.updateSensors(start, end);
	};
})();