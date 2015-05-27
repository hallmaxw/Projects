var express = require('express');

var app = express.Router();

app.get("/", function(req, res){
  res.send("Hello from root");
});

module.exports = app;
