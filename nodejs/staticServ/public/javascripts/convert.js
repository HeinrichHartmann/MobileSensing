(function() {
  
  var KKJ_ZONE_INFO = [
    [18,  500000],
    [21, 1500000],
    [24, 2500000],
    [27, 3500000],
    [30, 4500000],
    [33, 5500000],
  ]

  function KKJxyToWGS85lalo ( x, y ) {
    var kLaLo = KKJxyToKKJlalo( x, y );
    var wLaLo = KKJlaloToWGS84lalo( kLaLo.lat, kLaLo.lon );
    return wLaLo;
  }

  function KKJxyToKKJlalo ( x, y ) {
    if ( x < y ) {
      alert("Wrong coords!");
    }

    var zone = KKJZone( y );

    var LaLo = {}

    var minLa = deg2Rad( 59 );
    var maxLa = deg2Rad( 70.5 );
    var minLo = deg2Rad( 18.5 );
    var maxLo = deg2Rad( 32 );

    for (var i = 0; i < 35; i++) {
      var deltaLa = maxLa - minLa;
      var deltaLo = maxLo - minLo;

      LaLo.lat = rad2Deg( minLa + 0.5 * deltaLa );
      LaLo.lon = rad2Deg( minLo + 0.5 * deltaLo );

      var KKJt = KKJlaloToKKJxy ( LaLo, zone );

      if( KKJt.P < x ) {
        minLa = minLa + 0.45 * deltaLa;
      } else {
        maxLa = minLa + 0.55 * deltaLa;
      }

      if( KKJt.I < y ) {
        minLo = minLo + 0.45 * deltaLo;
      } else {
        maxLo = minLo + 0.55 * deltaLo;
      }
    };

    return {
      lat: LaLo.lat,
      lon: LaLo.lon
    };
  }

  function KKJlaloToKKJxy ( laLo, zone ) {
    var lo = deg2Rad( laLo.lon ) - deg2Rad( KKJ_ZONE_INFO[zone][0] );

    var a = 6378388;
    var f = 1 / 297;

    var b = ( 1.0 - f ) * a;
    var bb = b * b;
    var c = ( a / b ) * a;
    var ee = ( a * a - bb ) / bb;
    var n = ( a - b ) / ( a + b );
    var nn = n * n;

    var cosLa = Math.cos( deg2Rad( laLo.lat ) );
    var NN = ee * cosLa * cosLa;

    var LaF = Math.atan( Math.tan( deg2Rad( laLo.lat ) ) / 
      Math.cos( lo * Math.sqrt( 1 + NN ) ) );

    var cosLaF = Math.cos(LaF);
    var t = ( Math.tan( lo ) * cosLaF ) / Math.sqrt( 1 + ee * cosLaF * cosLaF );

    var A = a / ( 1 + n );
    var A1 = A * ( 1 + nn / 4 + nn * nn / 64  );
    var A2 = A * 1.5 * n * ( 1 - nn  / 8 );
    var A3 = A * 0.9375 * nn * ( 1 - nn / 4 );
    var A4 = A * 35 / 48 * nn * n;

    var out = {
      P: A1 * LaF -
         A2 * Math.sin( 2 * LaF ) +
         A3 * Math.sin( 4 * LaF ) -
         A4 * Math.sin( 6 * LaF ),

      I: c * Math.log( t + Math.sqrt( 1 + t * t ) ) + 500000 + zone * 1000000
    };

    return out;
  }

  function KKJlaloToWGS84lalo ( lat, lon ) {
    var dLa = deg2Rad( 0.124867E+01 +
            -0.269982E+00 * lat +
            0.191330E+00 * lon +
            0.356119E-02 * lat * lat +
            -0.122312E-02 * lat * lon +
            -0.335514E-03 * lon * lon ) / 3600.0;

    var dLo = deg2Rad( -0.286111E+02 +
            0.114183E+01 * lat +
            -0.581428E+00 * lon +
            -0.152421E-01 * lat * lat +
            0.118177E-01 * lat * lon +
            0.826646E-03 * lon * lon ) / 3600.0;

    var WGS = {
      lat: rad2Deg( deg2Rad( lat ) + dLa ),
      lon: rad2Deg( deg2Rad( lon ) + dLo )
    };

    return WGS;
  }

  function KKJZone ( y ) {
    var zone = Math.floor( y / 1000000 );
    if(zone < 0 || zone > 5)
      alert("Zone " + zone + " invalid.");
    return zone;
  }

  function deg2Rad ( deg ) {
    return deg * (Math.PI / 180);
  }

  function rad2Deg ( rad ) {
    return rad * ( 180 / Math.PI );
  }

  window.Convert = {
    KKJToWGS: KKJxyToWGS85lalo
  }

})();