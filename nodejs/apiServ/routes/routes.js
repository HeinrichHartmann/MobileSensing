var mysql = require('MYSQLConnection')
  , restify = require('restify');

// All allowed tables to access
var tables = ['gps', 'wifi', 'gsm', 'magneticfield', 'accelerometer', 'tags', 'networklocation', 'bluetooth', 'gyroscope'];
var checkTable = function (table) {
  return tables.indexOf(table) >= 0;
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
        res.send(500, 'Database connection error!' +  err);
        next();
        connection.end();
        return;
      }
      connection.query(query, function (err, result) {
        if(err) {
          res.send(500, 'Database connection error!' +  err);
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
        res.send(500, 'Database connection error!' +  err);
        next();
        connection.end();
        return;
      }
      connection.query(query, values, function (err, result) {
        if(err) {
          res.send(500, 'Database connection error!' +  err);
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

  /**
   * Handler for all the resources we can get for a UUID.
   */
  server.get(/([\d]+)\/(.*)/, function (req, res, next) {
    if(!checkTable(req.params[1])) {
      next();
      return;
    }

    var query = 'SELECT * FROM `' + req.params[1] + '` WHERE `uuid` = ?'
      , values = [req.params[0]]
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
          res.send(500, 'Database connection error!' +  err);
          next();
          connection.end();
          return;
        }
        connection.query(query, values, function (err, results) {
          if(err) {
            res.send(500, 'Database connection error!' +  err);
            next();
            connection.end();
            return;
          }
          res.send(results);
          next();
          connection.end();
        });
    });
  });
};

module.exports = register;