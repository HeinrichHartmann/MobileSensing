
/*
 * GET home page.
 */

exports.index = function(req, res){
  res.render('index', { });
};

exports.convert = function(req, res){
  res.render('convert', { });
};