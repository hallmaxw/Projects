var express = require('express');
var driver = require('./nearby_driver');
var bodyParser = require('body-parser');
var api = express.Router();


api.use(bodyParser.json());

api.get("/events", function(req, res){
  driver.getEvents(function(err, results){
    res.send(results);
  });
});

api.post("/events",function(req, res){
  var event = req.body;
  console.log(event);
  driver.addEvent(event, function(err, result){
    res.send(result);
  });
});

api.get("/users", function(req, res){
  driver.getUsers(function(err, results){
    res.send(results);
  });
});

api.post("/users", function(req, res){
  var user = req.body;
  console.log(user);
  driver.addUser(user, function(err, result){
    res.send(result);
  });
});

module.exports = api;
