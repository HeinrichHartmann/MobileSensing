(function () {
 	var apiUrl = "http://mobile-sensing.west.uni-koblenz.de:8888";

 	$('.insertData').click(function () {
 		$('.insertDataLabel').html("Importing data... This task should be done in a few seconds.");
 		$.ajax({
 			url: apiUrl + '/importData'
 		}).done(function (data) {
 			$('.insertDataLabel').html("Done importing data.");
 		});
 		fillUUID();
 	});

 	var scrollTo = function (hash) {
 		location.hash = '';
 		location.hash = '#' + hash;
 	};
 
 	var fillUUID = function () {
 		$('.uuid')
			.find('option')
			.remove()
			.end();
 		$.ajax({
	    	url: apiUrl + '/devices'
	    }).done(function (data) {
	    	$.each(data, function (id, item) {
	       		getGpsCount(item);
	    	});
	    });
 	};

 	var updateLastUpdate = function (item) {
 		var maxTs = 0;
 		var updateLabelHtml = $("#option-"+item.uuid).html();
 		var sensors = ['gps', 'wifi', 'gsm', 'magneticfield', 'accelerometer', 'tags', 'networklocation', 'bluetooth', 'gyroscope'];
 		for (var i = 0; i < sensors.length; i++) {
 			$.ajax({
 				url: apiUrl + '/' + sensors[i] + '/' + item.uuid + '/lastUpdate'
 			}).done(function (data) {
 				if(data && data.ts && data.ts > maxTs) {
 					maxTs = data.ts;
 					var d = new Date(maxTs);
 					try {
 						$("#option-" + item.uuid).html(updateLabelHtml.replace("??", d));
 					} catch(err) {
 						console.log(item);
 					}
 				}
 			});
 		};
 	};

 	var getGpsCount = function (item) {
      var $uuid = $('.uuid');
      $.ajax({
        url: apiUrl + '/gps/' + item.uuid + '/count'
      }).done(function (data) {
        if(item.uuid === 19) {
          $uuid.append('<option value="' + item.uuid + '" id="option-' + item.uuid + '" selected>' + item.model + ' | ' + item.uuid + ' | Samples: ' + data[0]["COUNT(*)"] + ' | Last Update: ??</option>');
          onUUIDChange();
          updateLastUpdate(item);
          return;
        }
        if(data[0]["COUNT(*)"] && data[0]["COUNT(*)"] !== 0) {
          $uuid.append('<option value="' + item.uuid + '" id="option-' + item.uuid + '">' + item.model + ' | ' + item.uuid + ' | Samples: ' + data[0]["COUNT(*)"] + ' | Last Update: ??</option>');
        } else {
          $uuid.append('<option value="' + item.uuid + '" id="option-' + item.uuid + '">' + item.model + ' | ' + item.uuid + ' | No GPS samples! | Last Update: ??</option>');
        }
        updateLastUpdate(item);
      });
    };

 	var sortFunc = function (a, b) {
      return (a[0]-b[0]);
    };

    var sortByTS = function (a, b) {
    	return (a.ts - b.ts);
    };

	function PlotTable (table, id, fieldPrefix) {
		this._table = table;
		this._$id = $(id);
		this._$count = $(id + 'Count');
		this._$count.html('0');
		this._fieldPrefix = fieldPrefix;
		this._options = {
			series: {
				lines: { show: true },
				points: { show: true }	
			},
			grid: {
				hoverable: true,
				clickable: true
			},
			xaxis: { mode: "time" },
			selection: {
	        	mode: "x"
	     	}
    	};
	}

	PlotTable.prototype.plot = function(start, end, forceLimit) {
		var self = this;
		var uuid = $('.uuid').val();
		// Create URL
		var url = apiUrl + '/' + this._table + '/' + uuid + '?from=' + start + '&to=' + end;
		if(forceLimit) {
			url += '&limit=' + forceLimit;
		}
		self._$count.html("Updating....")
		// Make request
		$.ajax({
			url: url
		}).done(function (data) {
			//Update count
			self._$count.html(data.length);

			// Create Plot
			var x = [];
		    var y = [];
		    var z = [];
		    var sampledData = self.sampleData(data);
		    self._$count.html(sampledData.length + ' / ' + data.length);
		    $.each(sampledData, function (id, item) {
		     	var ts = new Date(item.ts);
			    x.push([ts, item[self._fieldPrefix + 'x']]);
			    y.push([ts, item[self._fieldPrefix + 'y']]);
			    z.push([ts, item[self._fieldPrefix + 'z']]);
		    });
		    x.sort(sortFunc);
		    y.sort(sortFunc);
		    z.sort(sortFunc);

		    var plotData = [
		    	{ label: 'X', data: x },
		    	{ label: 'Y', data: y },
		    	{ label: 'Z', data: z }
		    ];
		    self._$id.bind("plotselected", self.zoom);
		    var plot = $.plot(self._$id, plotData, self._options);
		    self._plot = plot;
        	self._$id.bind("plothover", self.hover);
        	self._$id.bind("plotclick", self.click());
		})
	};

	PlotTable.prototype.sampleData = function(data) {
		var self = this;
		var ret = [];
		var max = 200;
		if(max >= data.length) {
			return data;
		}
		var step = Math.floor(data.length/200);
		var x = 0;
		var y = 0;
		var z = 0;
		var i = 0;
		var curr = null;
		$.each(data, function (id, item) {
			if(id === 0) {
				ret.push(item);
				return;
			}
			++i;
			x += item[self._fieldPrefix + 'x'];
			y += item[self._fieldPrefix + 'y'];
			z += item[self._fieldPrefix + 'z'];
			if(id % step === 0) {
				if(curr === null) {
					console.log("ERROR!");
				}
				x = x / i;
				y = y / i;
				z = z / i;
				curr[self._fieldPrefix + 'x'] = x;
				curr[self._fieldPrefix + 'y'] = y;
				curr[self._fieldPrefix + 'z'] = z;
				ret.push(item);
				x = 0;
				y = 0;
				z = 0;
				i = 0;
			} else if(id % Math.floor(step/2) === 0) {
				curr = item;
			}
		});
		return ret;
	};

	var previousPoint = null;
	PlotTable.prototype.hover = function (event, pos, item) {
		var showTooltip = function (x, y, contents) {
				$("<div id='tooltip'>" + contents + "</div>").css({
				position: "absolute",
				display: "none",
				top: y + 5,
				left: x + 5,
				border: "1px solid #fdd",
				padding: "2px",
				"background-color": "#fee",
				opacity: 0.80
			}).appendTo("body").fadeIn(200);
		};
		if (item) {
			if (previousPoint !== item.dataIndex) {

				previousPoint = item.dataIndex;

				$("#tooltip").remove();
				var x = item.datapoint[0],
				y = item.datapoint[1].toFixed(2);

				showTooltip(item.pageX, item.pageY,
					item.series.label + " at " + new Date(parseInt(x)) + " : " + y);
			}
		} else {
			$("#tooltip").remove();
			previousPoint = null;            
		}
	};


	PlotTable.prototype.click = function () {
		var self = this;

		return function(event, pos, item) {
			self._plot.unhighlight();
			map.removeMarker();
			if(item) {
				var x = item.datapoint[0];
				map.showMarker(parseInt(x));
				self._plot.highlight(item.series, item.datapoint);
				scrollTo('gps');
			}
		}
	};

	PlotTable.prototype.zoom = function (event, ranges) {
		window.updateSensors(ranges.xaxis.from, ranges.xaxis.to);
	};

	function TableTable (table, id, dataEachCb) {
		this._table = table;
		this._$id = $(id);
		this._$count = $(id + 'Count');
		this._ajaxDone = dataEachCb;
	}

	TableTable.prototype.fill = function(start, end, forceLimit) {
		var self = this;
		var uuid = $('.uuid').val();
		// Create URL
		var url = apiUrl + '/' + this._table + '/' + uuid + '?from=' + start + '&to=' + end;
		if(forceLimit) {
			url += '&limit=' + forceLimit;
		}
		self._$count.html("Updating....")
		self._$id
			.find('tbody tr')
			.remove()
			.end();
		// Make request
		$.ajax({
			url: url
		}).done(function (data) {
			self._$count.html(data.length);
			var $table = self._$id.find('tbody');
			data.sort(sortByTS);
			$.each(data, function(id, item) {
				var htmlString = self._ajaxDone(id, item);
				var element = $(htmlString);
				element.click(function () {
					map.removeMarker();
					map.showMarker(item.ts);
					scrollTo('gps');
				});
				$table.append(element);
			});
		});
	};

	var Accelerometer 	= new PlotTable('accelerometer', '.acc', 'acc');
	var Magnetemeter 	= new PlotTable('magneticfield', '.mag', 'field');
	var Gyroscope 		= new PlotTable('gyroscope', '.gyro', 'angspeed');
	var Tags			= new TableTable('tags', '.tags', function (id, item) {
		return ('<tr style="cursor: pointer;"><td>' + new Date(item.ts) + '</td><td>' + item.txt + '</td></tr>');
	});
	var Wifi 			= new TableTable('wifi', '.wifi', function(id, item) {
		return ('<tr style="cursor: pointer;"><td>' + new Date(item.ts) + '</td><td>' + item.bssid + '</td><td>' + item.ssid + '</td><td>' + item.cap + '</td><td>' + item.connected + '</td><td>' + item.freq + '</td><td>' + item.sigLevel + '</td></tr>');
	});
	var GSM				= new TableTable('gsm', '.gsm', function(id, item) {
		return ('<tr style="cursor: pointer;"><td>' + new Date(item.ts) + '</td><td>' + item.operator + '</td><td>' + item.lac + '</td><td>' + item.cid + '</td><td>' + item.rssi + '</td></tr>');
	});
	var Bluetooth 		= new TableTable('bluetooth', '.bluetooth', function (id, item) {
		return ('<tr style="cursor: pointer;"><td>' + new Date(item.ts) + '</td><td>' + item.address + '</td><td>' + item.class + '</td><td>' + item.name + '</td><td>' + item.rssi + '</td></tr>');
	});

	// Export
	window.updateSensors = function (start, end, forceLimit) {
		Accelerometer.plot(start, end, forceLimit);
		Magnetemeter.plot(start, end, forceLimit);
		Gyroscope.plot(start, end, forceLimit);
		Tags.fill(start, end, forceLimit);
		Wifi.fill(start, end, forceLimit);
		GSM.fill(start, end, forceLimit);
		Bluetooth.fill(start, end, forceLimit);
	};

	var onUUIDChange = function () {
		var uuid = $('.uuid').val();

		// Gps
	    $.ajax({
	        url: apiUrl + '/gps/' + uuid
	    }).done(function (data) {
	        map.reset(data);
	    });
	};

 	// Init
 	fillUUID();
	var map = new Map('map');
	$('.uuid').change(onUUIDChange);


})();