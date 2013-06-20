var mysql = require('MYSQLConnection')
  , rowTrans = require('./rowTransformation')
  , cronJob = require('cron').CronJob
  , Stream = require('stream');

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

  mysql.getConnection(function (err, connection) {
    if(err) {
      console.log('Error while getting connection to databse.', err);
      return;
    }

    // Stream creations
    var s = new Stream;
    s.writable = true;

    s.write = function (row) {
      rowTrans.transform(row);
    };

    s.end = function (buf) {
      rowTrans.finish();
      deleteDataInTable();
      connection.end();
      console.log('Done');
    };

    connection.query('SELECT * FROM `samples`')
      .stream({ highWaterMark: 80 })
      .pipe(s);
  });
};

module.exports = function () {
  console.log('Cron started');
  //new cronJob('*/5 * * * *', function () {
    console.log('Starting cron to clear data.')
    changeData();
  //}, null, true);
};