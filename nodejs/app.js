var restify = require('restify')
  , routes = require('./routes/routes');

require('./lib/cronTask')();

var server = restify.createServer({
  name: 'MobileSensing'
});

//routes(server);

server.listen(8080, function () {
    console.log('%s listening at %s', server.name, server.url);
});