var express = require('express');

var app = express();

app.use("/api", require('./api/api'));

app.get("/", function(req, res){
  res.send("Hello from root");
});


app.listen(3000);
console.log("Express started on port 3000.");
