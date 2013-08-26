(function () {
  var apiUrl = "http://localhost:8888";

  var $routes = $('.hRoutes');

  var transportMean = {
    1: "Bus, Helsinki internal",
    2: "Tram",
    3: "Bus, Espoo internal",
    4: "Bus, Vantaa internal",
    5: "Helsinki region",
    6: "Subway",
    12: "Commuter Train",
    36: "Bus, Kirkkonummi internal",
    39: "Bus, Kerava internal"
  }

  function initRoutes () {
    getRoutes();
    $routes.change(displayRoute);
  }

  function getRoutes () {
    $.ajax({
      url: apiUrl + '/routes'
    })
    .done(function (data) {
      $.each(data, function (index, route) {
        $routes.append('<option value="' + route.id + '"> ' + route.id + ' - ' + route.lineName + ' - ' + transportMean[route.transportMean] + '</option>');
      });
    });
  }

  function displayRoute () {
    var routeId = $routes.val();
    $.ajax({
      url: apiUrl + '/routes/' + routeId 
    })
    .done(function (data) {
      map.drawRoute(routeId, data.stopCodeDir1, data.stopCodeDir2);
    });
  }

  initRoutes();
})();