/// <reference path="typings/express/express.d.ts" />
/// <reference path="typings/node/node.d.ts" />

var express = require('express');

var app = express();
var http = require('http');
app.use("/api", require('./api/api'));
app.use("/", require('./app/app'));

app.listen(3000);
console.log("Express started on port 3000.");