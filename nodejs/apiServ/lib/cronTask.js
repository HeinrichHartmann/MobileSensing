var mysql = require('MYSQLConnection')
  , rowTrans = require('./rowTransformation')
  , cronJob = require('cron').CronJob;

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

var changeData = function () {
  console.log('Data transformation started..');
  var concurrentQueries = 0;
  var doneNr = 0;
  mysql.getConnection(function (err, connection) {
    
    if(err) {
      console.log('Error while getting connection to databse.', err);
      return;
    }
    var query = connection.query('SELECT * FROM `samples`');
    query
      .on('error', function (err) {
        console.log('Error while executing query!', err);
      })
      .on('result', function (row) {
        concurrentQueries += 1;
        if(concurrentQueries === 20) {
          connection.pause();
        }
        rowTrans(row, function (err) {
          if(err) {
            console.log(err);
          }
          concurrentQueries -= 1;
          doneNr += 1;
          if(doneNr % 1000 === 0) {
            console.log(doneNr + 'rows done!');
          }
          connection.resume();
        });
      })
      .on('end', function () {
        deleteDataInTable();
        connection.end();
      });
  });
};

module.exports = function () {
  console.log('Cron started');
  //new cronJob('*/5 * * * *', function () {
    console.log('Starting cron to clear data.')
    changeData();
  //}, null, true);
};