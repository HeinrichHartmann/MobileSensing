(function () {
  
  var apiUrl = 'http://127.0.0.1:8080';

  $(function () {
    // Get all UUIDS
    $.ajax({
      url: apiUrl + '/devices'
    }).done(function (data) {
      var uuid = $('.uuid');
      $.each(data, function (id, item) {
        uuid.append('<option>' + item.uuid + '</option>');
      });
    });

    $('.uuid').change(function (foo) {
      var uuid = $('.uuid').val();
      
      // Gps
      $.ajax({
        url: apiUrl + '/' + uuid + '/gps?limit=10' 
      }).done(function (data) {
        var $gps = $('.gps');
        $.each(data, function (id, item) {
          $gps.append('<p>acc:' + item.accuracy + '; alt: ' + item.alt + '; lon: ' + item.lon + '; lat: ' + item.lat + ';</p>');
        }); 
      });

      // Acc
      $.ajax({
        url: apiUrl + '/' + uuid + '/accelerometer'
      }).done(function (data) {
        var $acc = $('.acc');
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
        url: apiUrl + '/' + uuid + '/magneticfield'
      }).done(function (data) {
        var $mag = $('.mag');
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
        url: apiUrl + '/' + uuid + '/gyroscope'
      }).done(function (data) {
        var $gyro = $('.gyro');
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

    });
  });

})();