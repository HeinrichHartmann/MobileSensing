(function () {
  
  var apiUrl = 'http://mobile-sensing.west.uni-koblenz.de:8888';

  $(function () {
    $('.from').datetimepicker();
    $('.to').datetimepicker();

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
      return 'from=' + from + '&to=' + to;
    };

    var addLimit = function () {
      var limit = $('.limit').val();
      if(!limit || limit === 0) {
        return '';
      }
      return 'limit=' + limit;
    };

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
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        var plot = $.plot($acc, plotData);
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
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        var plot = $.plot($mag, plotData);
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
        var plotData = [
          { label: 'X', data: x },
          { label: 'Y', data: y },
          { label: 'Z', data: z }
        ];
        var plot = $.plot($gyro, plotData);
      });

    };

    $('.uuid').change(getData);
    $('.go').click(getData);
  });

})();