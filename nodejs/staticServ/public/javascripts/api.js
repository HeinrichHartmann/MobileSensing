// (function () {
  
//   var apiUrl = 'http://mobile-sensing.west.uni-koblenz.de:8888';

//   $(function () {
//     $('.from').datetimepicker();
//     $('.to').datetimepicker();

//     var map = new Map('map');

//     var sortFunc = function (a, b) {
//       return (a[0]-b[0]);
//     };

//     var sortByTS = function (a, b) {
//       return a.ts - b.ts;
//     };

//     // Get all UUIDS
//     $.ajax({
//       url: apiUrl + '/devices'
//     }).done(function (data) {
//       //var uuid = $('.uuid');
//       $.each(data, function (id, item) {
//         //uuid.append('<option value="' + item.uuid + '">' + item.uuid + ' ( <span id="gpsCount' + item.uuid + '"></span> )' + '</option>');
//         getGpsCount(item.uuid);
//       });
//     });

//     var getGpsCount = function (uuid) {
//       var $uuid = $('.uuid');
//       $.ajax({
//         url: apiUrl + '/gps/' + uuid + '/count'
//       }).done(function (data) {
//         if(uuid === 19) {
//           $uuid.append('<option value="' + uuid + '" selected>' + uuid + ' Samples: ' + data[0]["COUNT(*)"] + '</option>');
//           getData();
//           return;
//         }
//         if(data[0]["COUNT(*)"] && data[0]["COUNT(*)"] !== 0) {
//           $uuid.append('<option value="' + uuid + '">' + uuid + ' Samples: ' + data[0]["COUNT(*)"] + '</option>');
//         } else {
//           $uuid.append('<option value="' + uuid + '">' + uuid + ' No GPS samples!</option>');
//         }
//       });
//     };

//     var addTimeRange = function () {
//       var from = Date.parse($('.from').val());
//       var to = Date.parse($('.to').val());
//       if(isNaN(from) || isNaN(to))
//         return '';
//       return 'from=' + from + '&to=' + to;
//     };

//     var addLimit = function () {
//       var limit = $('.limit').val();
//       if(!limit || limit === 0) {
//         return 'limit=500';
//       }
//       return 'limit=' + limit;
//     };

//     var options = {
//       series: {
//         lines: { show: true },
//         points: { show: true }
//       },
//       grid: {
//         hoverable: true,
//         clickable: true
//       },
//       xaxis: { mode: "time" },
//       selection: {
//         mode: "x"
//       }
//     };

//     var showTooltip = function (x, y, contents) {
//       $("<div id='tooltip'>" + contents + "</div>").css({
//         position: "absolute",
//         display: "none",
//         top: y + 5,
//         left: x + 5,
//         border: "1px solid #fdd",
//         padding: "2px",
//         "background-color": "#fee",
//         opacity: 0.80
//       }).appendTo("body").fadeIn(200);
//     };

//     var hover = function (event, pos, item) {  
//       if (item) {
//         if (previousPoint != item.dataIndex) {

//           previousPoint = item.dataIndex;

//           $("#tooltip").remove();
//           var x = item.datapoint[0].toFixed(2),
//           y = item.datapoint[1].toFixed(2);

//           showTooltip(item.pageX, item.pageY,
//               item.series.label + " of " + new Date(parseInt(x)) + " = " + y);
//         }
//       } else {
//         $("#tooltip").remove();
//         previousPoint = null;            
//       }
//     };

//     var previousPoint = null;

//     var zoomFunctions = [];
//     var zoomEvent = function (event, ranges) {
//       for (var i = zoomFunctions.length - 1; i >= 0; i--) {
//         zoomFunctions[i](event, ranges);
//       };
//     };

//     window.globalTimeRange = zoomEvent;

//     var getData = function () {
//       zoomFunctions = [];
//       var uuid = $('.uuid').val();
      
//       // Gps
//       $.ajax({
//         url: apiUrl + '/gps/' + uuid + '?limit=10000&' + addTimeRange()
//       }).done(function (data) {
//         map.reset(data);
//       });

//       // Acc
//       $.ajax({
//         url: apiUrl + '/accelerometer/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         var $acc = $('.acc');
//         $('.accCount').html(data.length);
//         var x = [];
//         var y = [];
//         var z = [];
//         $.each(data, function (id, item) {
//           var ts = new Date(item.ts);
//           x.push([ts, item.accx]);
//           y.push([ts, item.accy]);
//           z.push([ts, item.accz]);
//         });
//         x.sort(sortFunc);
//         y.sort(sortFunc);
//         z.sort(sortFunc);
//         var plotData = [
//           { label: 'X', data: x },
//           { label: 'Y', data: y },
//           { label: 'Z', data: z }
//         ];
//         zoomFunctions.push(function (event, ranges) {
//           plot = $.plot($acc, plotData, $.extend(true, {}, options, {
//             xaxis: {
//               min: ranges.xaxis.from,
//               max: ranges.xaxis.to
//             }
//           }));
//         });
//         $acc.bind("plotselected", zoomEvent);
//         var plot = $.plot($acc, plotData, options);
//         $(".acc").bind("plothover", hover);
//       });

//       // Mag
//       $.ajax({
//         url: apiUrl + '/magneticfield/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         var $mag = $('.mag');
//         $('.magCount').html(data.length);
//         var x = [];
//         var y = [];
//         var z = [];
//         $.each(data, function (id, item) {
//           var ts = new Date(item.ts);
//           x.push([ts, item.fieldx]);
//           y.push([ts, item.fieldy]);
//           z.push([ts, item.fieldz]);
//         });
//         x.sort(sortFunc);
//         y.sort(sortFunc);
//         z.sort(sortFunc);
//         var plotData = [
//           { label: 'X', data: x },
//           { label: 'Y', data: y },
//           { label: 'Z', data: z }
//         ];
//         zoomFunctions.push(function (event, ranges) {
//             plot = $.plot($mag, plotData, $.extend(true, {}, options, {
//               xaxis: {
//                 min: ranges.xaxis.from,
//                 max: ranges.xaxis.to
//               }
//             }));
//         });
//         $mag.bind("plotselected", zoomEvent);
//         var plot = $.plot($mag, plotData, options);
//         $(".mag").bind("plothover", hover);
//       });

//       // Gyro
//       $.ajax({
//         url: apiUrl + '/gyroscope/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         var $gyro = $('.gyro');
//         $('.gyroCount').html(data.length);
//         var x = [];
//         var y = [];
//         var z = [];
//         $.each(data, function (id, item) {
//           var ts = new Date(item.ts);
//           x.push([ts, item.angspeedx]);
//           y.push([ts, item.angspeedy]);
//           z.push([ts, item.angspeedz]);
//         });
//         x.sort(sortFunc);
//         y.sort(sortFunc);
//         z.sort(sortFunc);
//         var plotData = [
//           { label: 'X', data: x },
//           { label: 'Y', data: y },
//           { label: 'Z', data: z }
//         ];
//         zoomFunctions.push(function (event, ranges) {
//             plot = $.plot($gyro, plotData, $.extend(true, {}, options, {
//               xaxis: {
//                 min: ranges.xaxis.from,
//                 max: ranges.xaxis.to
//               }
//             }));
//         });
//         $gyro.bind("plotselected", zoomEvent);
//         var plot = $.plot($gyro, plotData, options);
//         $(".gyro").bind("plothover", hover);
//       });

//       // Tags
//       $.ajax({
//         url: apiUrl + '/tags/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         $tags = $('.tags');
//         $('.tags tr').remove();
//         data.sort(sortByTS);
//         $.each(data, function (id, item) {
//           $tags.append('<tr><td>' + new Date(item.ts) + '</td><td>' + item.txt + '</td></tr>');
//         });
//       });

//       // Wifi
//       $.ajax({
//         url: apiUrl + '/wifi/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         $tags = $('.wifi');
//         $('.wifi tr').remove();
//         data.sort(sortByTS);
//         $.each(data, function (id, item) {
//           $tags.append('<tr><td>' + new Date(item.ts) + '</td><td>' + item.bssid + '</td><td>' + item.ssid + '</td><td>' + item.cap + '</td><td>' + item.connected + '</td><td>' + item.freq + '</td><td>' + item.sigLevel + '</td></tr>');
//         });
//       });

//       // gsm
//       $.ajax({
//         url: apiUrl + '/gsm/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         $tags = $('.gsm');
//         $('.gsm tr').remove();
//         data.sort(sortByTS);
//         $.each(data, function (id, item) {
//           $tags.append('<tr><td>' + new Date(item.ts) + '</td><td>' + item.operator + '</td><td>' + item.lac + '</td><td>' + item.cid + '</td><td>' + item.rssi + '</td></tr>');
//         });
//       });

//       // Bluetooth
//       $.ajax({
//         url: apiUrl + '/bluetooth/' + uuid + '?' + addTimeRange() + '&' + addLimit()
//       }).done(function (data) {
//         $tags = $('.bluetooth');
//         $('.bluetooth tr').remove();
//         data.sort(sortByTS);
//         $.each(data, function (id, item) {
//           $tags.append('<tr><td>' + new Date(item.ts) + '</td><td>' + item.address + '</td><td>' + item.class + '</td><td>' + item.name + '</td><td>' + item.rssi + '</td></tr>');
//         });
//       });

//     };

//     $('.uuid').change(getData);
//     $('.go').click(getData);
//   });

// })();