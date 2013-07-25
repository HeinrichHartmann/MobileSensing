var mysql = require('MYSQLConnection')
  , rowTrans = require('./rowTransformation')
  , cronJob = require('cron').CronJob
  , Stream = require('stream');

var rowsPerPage = 50000;

var deleteDataInTable = function () {
  mysql.getConnection(function (err, connection) {
    if(err) {
      console.log('Error while getting connection to databse.', err);
      return;
    }
    var query = connection.query('TRUNCATE TABLE `samples`', function (err, rows, fields) {
      if(err) {
        console.log('Error while clearing table!', err);
        return;
      }
      console.log('Table samples cleared!');
      connection.end();
    });
  });
};

// Querys the database for all rows in `samples` and does some transformation on them
var changeData = function () {
  console.log('Data transformation started..');
  var concurrentQueries = 0;
  var doneNr = 0;
  var pageNr = 0;

  var startQuery = function (page) {
    console.log('Start Page ' + page)
    mysql.getConnection(function (err, connection) {
      if(err) {
        console.log('Error while getting connection to databse.', err);
        return;
      }

      var stratNumber = doneNr;

      // Stream creations
      var s = new Stream;
      s.writable = true;

      s.write = function (row) {
        doneNr += 1;
        rowTrans.transform(row);
      };

      s.end = function (buf) {
        if(doneNr !== stratNumber) {
          rowTrans.finish();
          //
          console.log('Done Page ' + page);
          pageNr += 1;
          startQuery(pageNr);
        } else {
          deleteDataInTable();
        }
        connection.end();
      };
      connection.query('SELECT * FROM `samples` LIMIT ?, ?', [page * rowsPerPage, rowsPerPage])
        .stream({ highWaterMark: 80 })
        .pipe(s);
    });
  };

  startQuery(pageNr);
};

module.exports = {
  cron: function () {
    console.log('Cron started');
    new cronJob('*/5 * * * *', function () {
      console.log('Starting cron to clear data.')
      changeData();
    }, null, true);
  },

  changeData: changeData
};