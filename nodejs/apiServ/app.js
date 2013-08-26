var restify = require('restify')
  , routes = require('./routes/routes')
  , hRoutes = require('./routes/helsinkiRoutes');

require('./lib/cronTask').cron();

var server = restify.createServer({
  name: 'MobileSensing'
});
server.use(restify.queryParser());
server.use(restify.CORS( {credentials: true} ));
server.use(restify.fullResponse());

routes(server);
hRoutes(server);

server.listen(8888, function () {
    console.log('%s listening at %s', server.name, server.url);
});