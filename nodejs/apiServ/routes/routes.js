var mysql = require('MYSQLConnection')
  , restify = require('restify')
  , cronTask = require('../lib/cronTask');

// All allowed tables to access
var tables = ['gps', 'wifi', 'gsm', 'magneticfield', 'accelerometer', 'tags', 'networklocation', 'bluetooth', 'gyroscope'];
var checkTable = function (table) {
  return tables.indexOf(table) >= 0;
};

var sendDatabaseError = function(err, res) {
  res.send(500, "Database error! " + err);
}

var getCountFunction = function (table) {
  return function (req, res, next) {
    var query = 'SELECT COUNT(*) FROM `' + table + '` WHERE `uuid` = ?';
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      connection.query(query, [req.params.uuid], function (err, result) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }
        res.send(result);
        next();
        connection.end();
      });
    });
  };
};

var getDataFunction = function (table) {
  return function (req, res, next) {
    var query = 'SELECT * FROM `' + table + '` WHERE `uuid` = ?'
      , values = [req.params.uuid]
      , from = req.query.from
      , to = req.query.to
      , limit = req.query.limit;

    if(from) {
      query += ' AND `ts` >= ?';
      values.push(from);
    }

    if(to) {
      query += ' AND `ts` <= ?';
      values.push(to);
    }

    if(limit) {
      limit = parseInt(limit);
      if(limit > 0)
        query += ' LIMIT ' + limit;
      
    }

    mysql.getConnection(function (err, connection) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }
        connection.query(query, values, function (err, results) {
          if(err) {
            sendDatabaseError(err, res);
            next();
            connection.end();
            return;
          }
          for (var i = results.length - 1; i >= 0; i--) {
            delete results[i]["prio"];
            delete results[i]["synced"];
            delete results[i]["dataclass"];
          };
          res.send(results);
          next();
          connection.end();
        });
    });
  };
};

var getNewestFunction = function (table) {
  return function (req, res, next) {
    var query = 'SELECT MAX(TS) FROM `' + table + '` WHERE `uuid` = ?';
    var values = [req.params.uuid];

    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        connection.end();
        next();
        return;
      }
      connection.query(query, values, function (err, results) {
        if(err) {
          sendDatabaseError(err, res);
          connection.end();
          next();
          return;
        }
        var max = {
          ts: results[0]["MAX(TS)"]
        };
        res.send(max);
        connection.end();
        next();
      });
    });
  };
};

var getNearestFunction = function (table, extraFields) {
  return function (req, res, next) {
    var selectNearestTag = "SELECT ABS( ts - ? ) AS a, "
                         + extraFields + " "
                         + " FROM  `" + table + "` "
                         + " WHERE  `uuid` = ? "
                         + " AND  `ts` "
                         + "   BETWEEN ? "
                         + "   AND ? "
                         + " ORDER BY a "
                         + "  LIMIT 1";
    var values = [req.params.ts, req.params.uuid, parseInt(req.params.ts) - parseInt(req.params.variance), parseInt(req.params.ts) + parseInt(req.params.variance)];
    mysql.getConnection(function (err, connection) {
      if(err) {
        console.log("CONNECTIONS");
        sendDatabaseError(err, res);
        connection.end();
        next();
        return;
      }
      connection.query(selectNearestTag, values, function(err, result) {
        if(err) {
          sendDatabaseError(err, res);
          connection.end();
          next();
          return;
        }
        res.send(result);
        connection.end();
        next();
      });
    });
  };
};

/**
 * Registers all routes we need with the server
 * @param  {Restify Server} server The restify server
 */
var register = function (server) {
  server.get('/devices', function (req, res, next) {
    var query = 'SELECT * FROM `devinfo`';
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      connection.query(query, function (err, result) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }

        res.send(result);
        next();
        connection.end();
      });
    });
  });

  server.get('/devices/:uuid', function (req, res, next) {
    var query = 'SELECT * FROM `devinfo` WHERE `uuid` = ? OR `textuuid` = ?';
    var values = [req.params.uuid, req.params.uuid];
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      connection.query(query, values, function (err, result) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }

        res.send(result);
        next();
        connection.end();
      });
    });
  });

  // Register the Row count and data function for every table
  for (var i = tables.length - 1; i >= 0; i--) {
    server.get('/'+tables[i]+'/:uuid/count', getCountFunction(tables[i]));
    server.get('/'+tables[i]+'/:uuid', getDataFunction(tables[i]));
    server.get('/'+tables[i]+'/:uuid/lastUpdate', getNewestFunction(tables[i]));
  };

  // tags next to Timestamp
  server.get('/tags/:uuid/nearestTo/:ts/variance/:variance', getNearestFunction('tags', ' txt '));
  server.get('/gps/:uuid/nearestTo/:ts/variance/:variance', getNearestFunction('gps', ' lat, lon '));

  server.get('/importData', function (req, res, next) {
    cronTask.changeData(function () {
      res.send(200, "Importing data.");
    });
  });

};

module.exports = register;