var mysql = require('MYSQLConnection');

// Regex to get data out of the xml string
var regex = {
  'GPS': /<accuracy>(.*)<\/accuracy><alt>(.*)<\/alt><lat>(.*)<\/lat><lon>(.*)<\/lon>(<speed>(.*)<\/speed>)?/,
  'GSM': /(<operator>(.*)<\/operator>)?<neighbors class=\\?'java.util.Vector\\?'>(.*)<\/neighbors><lac>(.*)<\/lac><cid>(.*)<\/cid><rssi>(.*)<\/rssi>/,
  'MagneticField': /<fieldX>(.+\..+)<\/fieldX><fieldY>(.+\..+)<\/fieldY><fieldZ>(.+\..+)<\/fieldZ>/,
  'Accelerometer': /<accX>(.+\..+)<\/accX><accY>(.+\..+)<\/accY><accZ>(.+\..+)<\/accZ>/,
  'Wifi': /<bssid>(.*)<\/bssid>(<ssid>(.*)<\/ssid>)?(<cap>(.*)<\/cap>)?<connected>(.*)<\/connected><freq>(.*)<\/freq><sigLevel>(.*)<\/sigLevel>/,
  'Tags': /<txt>(.*)<\/txt>/,
  'NetworkLocation': /<accuracy>(.*)<\/accuracy><lat>(.*)<\/lat><lon>(.*)<\/lon>/,
  'Bluetooth': /<adress>(.*)<\/adress><class>(.*)<\/class>(<name>(.*)<\/name>)?<rssi>(.*)<\/rssi>/,
  'Gyroscope': /<angSpeedX>(.+\..+)<\/angSpeedX><angSpeedY>(.+\..+)<\/angSpeedY><angSpeedZ>(.+\..+)<\/angSpeedZ>/
};

// executes the query
var execQuery = function () {
  var queryBuffer = [];
  var valueBuffer = [];
  var bufferedQueries = 0;
  var queriesDone = 0;

  return function (q, values) {
    if(!q) {
      mysql.getConnection(function (err, connection) {
        if(err) {
          console.log("Error while establishing database connection!");
          console.log(err);
          return;
        }
        if(queryBuffer === '') {
          connection.end();
          return;
        }
        var queries = '';
        var v = [];
        var done = 0;
        for (var i = 0; i < 1000 && queryBuffer.length > 0 && valueBuffer.length > 0; i++) {
          queries += queryBuffer.shift();
          v = v.concat(valueBuffer.shift());
          done++;
        };
        connection.query(queries, v, function (err, results) {
          if(err) {
            console.log('Error while executing query');
            console.log(err);
            return;
          }
          queriesDone += 1000;
          console.log('Query done. ' + queriesDone);
          connection.end();
        });
      });
      return;
    }
    queryBuffer.push(q);
    valueBuffer.push(values);
    bufferedQueries += 1;
    if(bufferedQueries > 1000) {
      bufferedQueries -= 1000;
      execQuery();
    }
  };
}();

// Gets the data out of every row dependent of the sensorid.
var rowSensorTransform = {
  'GPS': function (row) {
    var result = regex['GPS'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! GPS went wrong?', row);
      return;
    }
    var acc = result[1]
      , alt = result[2]
      , lat = result[3]
      , lon = result[4]
      , speed = result[6] || 0;

    var q = "INSERT INTO `gps` (`uuid`, `ts`, `accuracy`, `alt`, `lat`, `lon`, `speed`, `prio`, `synced`, `dataclass`) " +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, acc, alt, lat, lon, speed, row.prio, row.synced, row.dataclass];

    execQuery(q, values);
  },

  'GSM': function (row) {
    var result = regex['GSM'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! GSM went wrong?', row);
      return;
    }
    var op = result[2] || ''
      , ne = result[3]
      , lac = result[4]
      , cid = result[5]
      , rssi = result[6];

    var q = "INSERT INTO `gsm` (`uuid`, `ts`, `operator`, `lac`, `cid`, `rssi`, `neighbors`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );\n";
    var values = [row.uuid, row.ts, op, lac, cid, rssi, ne, row.prio, row.synced, row.dataclass];

    execQuery(q, values);
  },

  'MagneticField': function (row) {
    var result = regex['MagneticField'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! MagneticField went wrong?', row);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `magneticfield` (`uuid`, `ts`, `fieldx`, `fieldy`, `fieldz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },

  'Accelerometer': function (row) {
    var result = regex['Accelerometer'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Accelerometer went wrong?', row);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `accelerometer` (`uuid`, `ts`, `accx`, `accy`, `accz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },

  'Wifi': function (row) {
    var result = regex['Wifi'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! WIFI went wrong?', row);
      return;
    }
    var bssid = result[1]
      , ssid = result[3] || '' 
      , cap = result[5] || ''
      , connected = result[6] === 'true' ? true : false
      , freq = result[7]
      , sig = result[8];

    var q = "INSERT INTO `wifi` (`uuid`, `ts`, `bssid`, `ssid`, `cap`, `connected`, `freq`, `sigLevel`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, bssid, ssid, cap, connected, freq, sig, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },


  'Tags': function (row) {
    var result = regex['Tags'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Tags went wrong?', row);
      return;
    }
    var txt = result[1];

    var q = "INSERT INTO `tags` (`uuid`, `ts`, `txt`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, txt, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },

  
  'NetworkLocation': function (row) {
    var result = regex['NetworkLocation'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! NetworkLocation went wrong?', row);
      return;
    }
    var acc = result[1]
      , lat = result[2]
      , lon = result[3];

    var q = "INSERT INTO `networklocation` (`uuid`, `ts`, `accuracy`, `lat`, `lon`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, acc, lat, lon, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },


  'Bluetooth': function (row) {
    var result = regex['Bluetooth'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! BLUETOOTH went wrong?', row);
      return;
    }
    var address = result[1]
      , klass = result[2]
      , name = result[4] || ''
      , rssi = result[5];

    var q = "INSERT INTO `bluetooth` (`uuid`, `ts`, `address`, `class`, `name`, `rssi`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, address, klass, name, rssi, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  },

  'Gyroscope': function (row) {
    var result = regex['Gyroscope'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Gyroscope went wrong?', row);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `gyroscope` (`uuid`, `ts`, `angspeedx`, `angspeedy`, `angspeedz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );\n";

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values);
  }

};


module.exports = {
  transform: function (row) {
    if(row.sensorid !== 'TimeSyncStateChanges') {
      rowSensorTransform[row.sensorid](row);
    }
  },
  finish: function () {
    execQuery();
  }
};
