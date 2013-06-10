var restify = require('restify')
  , routes = require('./routes/routes');

//require('./lib/cronTask')();

var server = restify.createServer({
  name: 'MobileSensing'
});
server.use(restify.queryParser());
server.use(restify.CORS( {credentials: true} ));
server.use(restify.fullResponse());

routes(server);

server.listen(8080, function () {
    console.log('%s listening at %s', server.name, server.url);
});