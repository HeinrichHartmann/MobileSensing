$('#conv').click(function () {
  var x = $('#kkjx').val();
  var y = $('#kkjy').val();
  var laLo = Convert.KKJToWGS(x, y);
  $('#latlon').html("Lat: " + laLo.lat + " Lon: " + laLo.lon);
});