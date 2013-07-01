(function () {
  
  var apiUrl = 'http://mobile-sensing.west.uni-koblenz.de:8888';

  $(function () {
    $('.from').datetimepicker();
    $('.to').datetimepicker();

    var sortFunc = function (a, b) {
      return (a[0]-b[0]);
    };

    // Get all UUIDS
    $.ajax({
      url: apiUrl + '/devices'
    }).done(function (data) {
      var uuid = $('.uuid');
      $.each(data, function (id, item) {
        uuid.append('<option value="' + item.uuid + '">' + item.uuid + ' ( ' + item.device + ' )' + '</option>');
      });
    });

    var addTimeRange = function () {
      var from = Date.parse($('.from').val());
      var to = Date.parse($('.to').val());
      if(isNaN(from) || isNaN(to))
        return '';
      return 'from=' + from + '&to=' + to;
    };

    var addLimit = function () {
      var limit = $('.limit').val();
      if(!limit || limit === 0) {
        return '';
      }
      return 'limit=' + limit;
    };

    var options = {
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

    function showTooltip(x, y, contents) {
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
    }

    var hover = function (event, pos, item) {  
      if (item) {
        if (previousPoint != item.dataIndex) {

          previousPoint = item.dataIndex;

          $("#tooltip").remove();
          var x = item.datapoint[0].toFixed(2),
          y = item.datapoint[1].toFixed(2);

          showTooltip(item.pageX, item.pageY,
              item.series.label + " of " + x + " = " + y);
        }
      } else {
        $("#tooltip").remove();
        previousPoint = null;            
      }
    };

    var previousPoint = null;

    var getData = function (foo) {
      var uuid = $('.uuid').val();
      
      // Gps
      $.ajax({
        url: apiUrl + '/' + uuid + '/gps?limit=10&' + addTimeRange()
      }).done(function (data) {
        var $gps = $('.gps');
        $.each(data, function (id, item) {
          $gps.append('<p>acc:' + item.accuracy + '; alt: ' + item.alt + '; lon: ' + item.lon + '; lat: ' + item.lat + ';</p>');
        }); 
      });

      // Acc
      $.ajax({
        url: apiUrl + '/' + uuid + '/accelerometer?' + addTimeRange() + '&' + addLimit()
      }).done(function (data) {
        var $acc = $('.acc');
        $('.magCount').val(data.length);
        var x = [];
        var y = [];
        var z = [];
        $.each(data, function (id, item) {
          var ts = new Date(item.ts);
          x.push([ts, item.accx]);
          y.push([ts, item.accy]);
          z.push([ts, item.accz]);
        });
        x.sort(sortFunc);
        y.sort(sortFunc);
        z.sort(sortFunc);
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        $acc.bind("plotselected", function (event, ranges) {
            plot = $.plot($acc, plotData, $.extend(true, {}, options, {
              xaxis: {
                min: ranges.xaxis.from,
                max: ranges.xaxis.to
              }
            }));
        });
        var plot = $.plot($acc, plotData, options);
        $(".acc").bind("plothover", hover);
      });

      // Mag
      $.ajax({
        url: apiUrl + '/' + uuid + '/magneticfield?' + addTimeRange() + '&' + addLimit()
      }).done(function (data) {
        var $mag = $('.mag');
        $('.magCount').val(data.length);
        var x = [];
        var y = [];
        var z = [];
        $.each(data, function (id, item) {
          var ts = new Date(item.ts);
          x.push([ts, item.fieldx]);
          y.push([ts, item.fieldy]);
          z.push([ts, item.fieldz]);
        });
        x.sort(sortFunc);
        y.sort(sortFunc);
        z.sort(sortFunc);
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        $mag.bind("plotselected", function (event, ranges) {
            plot = $.plot($mag, plotData, $.extend(true, {}, options, {
              xaxis: {
                min: ranges.xaxis.from,
                max: ranges.xaxis.to
              }
            }));
        });
        var plot = $.plot($mag, plotData, options);
        $(".mag").bind("plothover", hover);
      });

      // Gyro
      $.ajax({
        url: apiUrl + '/' + uuid + '/gyroscope?' + addTimeRange() + '&' + addLimit()
      }).done(function (data) {
        var $gyro = $('.gyro');
        $('.gyroCount').val(data.length);
        var x = [];
        var y = [];
        var z = [];
        $.each(data, function (id, item) {
          var ts = new Date(item.ts);
          x.push([ts, item.angspeedx]);
          y.push([ts, item.angspeedy]);
          z.push([ts, item.angspeedz]);
        });
        x.sort(sortFunc);
        y.sort(sortFunc);
        z.sort(sortFunc);
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        $gyro.bind("plotselected", function (event, ranges) {
            plot = $.plot($gyro, plotData, $.extend(true, {}, options, {
              xaxis: {
                min: ranges.xaxis.from,
                max: ranges.xaxis.to
              }
            }));
        });
        var plot = $.plot($gyro, plotData, options);
        $(".gyro").bind("plothover", hover);
      });

    };

    $('.uuid').change(getData);
    $('.go').click(getData);
  });

})();