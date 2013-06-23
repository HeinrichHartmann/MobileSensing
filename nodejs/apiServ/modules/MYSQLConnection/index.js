// This just connects to the mysql server and exports the pool.getConnection function so we can use it easily
var mysql = require('mysql');

var pool = mysql.createPool({
  host: 'localhost',
  database: 'liveandgov',
  user: 'chrisschaefer',
  password: '00chrisschaefer00',
  connectionLimit: 100,
  multipleStatements: true
});

module.exports = pool;