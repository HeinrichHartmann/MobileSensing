// This just connects to the mysql server and exports the pool.getConnection function so we can use it easily
var mysql = require('mysql');

var pool = mysql.createPool({
  host: '192.168.1.51',
  database: 'liveandgov',
  user: 'root',
  password: '',
  connectionLimit: 100,
  multipleStatements: true
});

var routesPool = mysql.createPool({
  host: '192.168.1.51',
  database: 'helsinki',
  user: 'root',
  password: ''
});

var p = pool;
p.routes = routesPool;

module.exports = p;