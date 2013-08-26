var mysql = require('MYSQLConnection').routes
  , restify = require('restify');

var sendDatabaseError = function(err, res) {
  res.send(500, "Database error! " + err);
}

var register = function (server) {
  server.get('/routes', function (req, res) {
      mysql.getConnection(function (err, connection) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }
        var query = "SELECT DISTINCT (id), lineName, transportMean FROM  `lines` WHERE  `language` =  's'";
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

  server.get('/routes/:id', function (req, res) {
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      var query = "SELECT DISTINCT (id), lineName, transportMean, stopCodeDir1, stopCodeDir2 FROM  `lines` WHERE  `language` = 's' AND `id` = ?";
      connection.query(query, [req.params.id], function (err, result) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }
        res.send(result[0]);
        next();
        connection.end();
      });
    });
  });

  server.get('/routes/:id/route', function (req, res) {
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      var query = "SELECT type, x, y, routeDir, stopCode  FROM  `routes` WHERE  `routeCode` = ? ORDER BY routeDir, stopOrder";
      connection.query(query, [req.params.id], function (err, result) {
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

  server.get('/stops/:stopCode', function (req, res) {
    mysql.getConnection(function (err, connection) {
      if(err) {
        sendDatabaseError(err, res);
        next();
        connection.end();
        return;
      }
      var query = "SELECT latitude, longitude, stopName FROM `stops` WHERE `stopCode` = ?";
      connection.query(query, [req.params.stopCode], function (err, result) {
        if(err) {
          sendDatabaseError(err, res);
          next();
          connection.end();
          return;
        }
        res.send(result[0]);
        next();
        connection.end();
      });
    });
  });

};

module.exports = register;