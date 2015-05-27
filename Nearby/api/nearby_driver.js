var MongoClient = require('mongodb').MongoClient,
	assert = require('assert'),
	ObjectId = require('mongodb').ObjectID;
var url = "mongodb://localhost:27017/nearby";

function getUsers(callback){
	MongoClient.connect(url, function(err, db){
		assert.equal(err, null);
		db.collection('users').find(function(err, result){
			assert.equal(err, null);
			result.toArray(function(err, results){
				assert.equal(err, null);
				db.close();
				callback(null, results);
			});
		});
	});
}

function addUser(user, callback){
	MongoClient.connect(url, function(err, db){
		assert.equal(err, null);
		db.collection('users').insert(user, function(err, result){
			assert.equal(err, null);
			callback(err, result);
		});
	});
}

function getEvents(callback){
	MongoClient.connect(url, function(err, db){
		assert.equal(err, null);
		db.collection('events').find(function(err, result){
			assert.equal(err, null);
			result.toArray(function(err, results){
				assert.equal(err, null);
				db.close();
				callback(null, results);
			});
		});
	});
}

function addEvent(event, callback){
	event['user_id'] = new ObjectId(event['user_id']);
	MongoClient.connect(url, function(err, db){
		assert.equal(err, null);
		db.collection('events').insert(event, function(err, result){
			assert.equal(err, null);
			callback(err, result);
		});
	});
}
module.exports = {getUsers: getUsers, addUser: addUser, getEvents: getEvents, addEvent: addEvent};