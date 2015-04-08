var express = require('express');

var api = express.Router();

api.get("/activities", function(req, res){
  res.send('List of activities.');
});

api.get("/users", function(req, res){
  res.send('List of users.');
});

module.exports = api;
