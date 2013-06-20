var mysql = require('MYSQLConnection');

// Regex to get data out of the xml string
var regex = {
  'GPS': /<accuracy>(.*)<\/accuracy><alt>(.*)<\/alt><lat>(.*)<\/lat><lon>(.*)<\/lon>(<speed>(.*)<\/speed>)?/,
  'GSM': /<operator>(.*)<\/operator><neighbors class='java.util.Vector'>(.*)<\/neighbors><lac>(.*)<\/lac><cid>(.*)<\/cid><rssi>(.*)<\/rssi>/,
  'MagneticField': /<fieldX>(.+\..+)<\/fieldX><fieldY>(.+\..+)<\/fieldY><fieldZ>(.+\..+)<\/fieldZ>/,
  'Accelerometer': /<accX>(.+\..+)<\/accX><accY>(.+\..+)<\/accY><accZ>(.+\..+)<\/accZ>/,
  'Wifi': /<bssid>(.*)<\/bssid>(<ssid>(.*)<\/ssid>)?(<cap>(.*)<\/cap>)?<connected>(.*)<\/connected><freq>(.*)<\/freq><sigLevel>(.*)<\/sigLevel>/,
  'Tags': /<txt>(.*)<\/txt>/,
  'NetworkLocation': /<accuracy>(.*)<\/accuracy><lat>(.*)<\/lat><lon>(.*)<\/lon>/,
  'Bluetooth': /<adress>(.*)<\/adress><class>(.*)<\/class>(<name>(.*)<\/name>)?<rssi>(.*)<\/rssi>/,
  'Gyroscope': /<angSpeedX>(.+\..+)<\/angSpeedX><angSpeedY>(.+\..+)<\/angSpeedY><angSpeedZ>(.+\..+)<\/angSpeedZ>/
};

// executes the query
var execQuery = function (q, values, callback) {
  mysql.getConnection(function (err, connection) {
    connection.query(q, values, function (err, result) {
      if(err) {
        console.log('Error at query ', q)
        callback(err);
        return;
      }
      connection.end();
      callback();
    });
  });
};

// Gets the data out of every row dependent of the sensorid.
var rowSensorTransform = {
  'GPS': function (row, callback) {
    var result = regex['GPS'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! GPS went wrong?', row);
      callback(true);
      return;
    }
    var acc = result[1]
      , alt = result[2]
      , lat = result[3]
      , lon = result[4]
      , speed = result[6] || 0;

    var q = "INSERT INTO `gps` (`uuid`, `ts`, `accuracy`, `alt`, `lat`, `lon`, `speed`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, acc, alt, lat, lon, speed, row.prio, row.synced, row.dataclass];

    execQuery(q, values, callback);
  },

  'GSM': function (row, callback) {
    var result = regex['GSM'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! GSM went wrong?', row);
      callback(true);
      return;
    }
    var op = result[1]
      , ne = result[2]
      , lac = result[3]
      , cid = result[4]
      , rssi = result[5];

    var q = "INSERT INTO `gsm` (`uuid`, `ts`, `operator`, `lac`, `cid`, `rssi`, `neighbors`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );"
    var values = [row.uuid, row.ts, op, lac, cid, rssi, ne, row.prio, row.synced, row.dataclass];

    execQuery(q, values, callback);
  },

  'MagneticField': function (row, callback) {
    var result = regex['MagneticField'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! MagneticField went wrong?', row);
      callback(true);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `magneticfield` (`uuid`, `ts`, `fieldx`, `fieldy`, `fieldz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },

  'Accelerometer': function (row, callback) {
    var result = regex['Accelerometer'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Accelerometer went wrong?', row);
      callback(true);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `accelerometer` (`uuid`, `ts`, `accx`, `accy`, `accz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },

  'Wifi': function (row, callback) {
    var result = regex['Wifi'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! WIFI went wrong?', row);
      callback(true);
      return;
    }
    var bssid = result[1]
      , ssid = result[3] || '' 
      , cap = result[5] || ''
      , connected = result[6] === 'true' ? true : false
      , freq = result[7]
      , sig = result[8];

    var q = "INSERT INTO `wifi` (`uuid`, `ts`, `bssid`, `ssid`, `cap`, `connected`, `freq`, `sigLevel`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, bssid, ssid, cap, connected, freq, sig, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },


  'Tags': function (row, callback) {
    var result = regex['Tags'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Tags went wrong?', row);
      callback(true);
      return;
    }
    var txt = result[1];

    var q = "INSERT INTO `tags` (`uuid`, `ts`, `txt`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, txt, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },

  
  'NetworkLocation': function (row, callback) {
    var result = regex['NetworkLocation'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! NetworkLocation went wrong?', row);
      callback(true);
      return;
    }
    var acc = result[1]
      , lat = result[2]
      , lon = result[3];

    var q = "INSERT INTO `networklocation` (`uuid`, `ts`, `accuracy`, `lat`, `lon`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, acc, lat, lon, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },


  'Bluetooth': function (row, callback) {
    var result = regex['Bluetooth'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! BLUETOOTH went wrong?', row);
      callback(true);
      return;
    }
    var address = result[1]
      , klass = result[2]
      , name = result[4] || ''
      , rssi = result[5];

    var q = "INSERT INTO `bluetooth` (`uuid`, `ts`, `address`, `class`, `name`, `rssi`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, address, klass, name, rssi, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  },

  'Gyroscope': function (row, callback) {
    var result = regex['Gyroscope'].exec(row.data);
    if(!result) {
      console.log('ERROR! Whoops! Gyroscope went wrong?', row);
      callback(true);
      return;
    }
    var x = result[1]
      , y = result[2]
      , z = result[3];

    var q = "INSERT INTO `gyroscope` (`uuid`, `ts`, `angspeedx`, `angspeedy`, `angspeedz`, `prio`, `synced`, `dataclass`)" +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ? );"

    var values = [row.uuid, row.ts, x, y, z, row.prio, row.synced, row.dataclass];
    execQuery(q, values, callback);
  }

};


module.exports = function (row, callback) {
  rowSensorTransform[row.sensorid](row, callback);
};
