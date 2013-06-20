// This just connects to the mysql server and exports the pool.getConnection function so we can use it easily
var mysql = require('mysql');

var pool = mysql.createPool({
  host: '192.168.178.22',
  database: 'liveandgov',
  user: 'root',
  password: '',
  connectionLimit: 100
});

module.exports = pool;