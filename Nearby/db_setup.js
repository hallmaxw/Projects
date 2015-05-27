/// <reference path="typings/express/express.d.ts" />
/// <reference path="typings/node/node.d.ts" />
/// <reference path="typings/mongodb/mongodb.d.ts" />

var MongoClient = require('mongodb').MongoClient,
	assert = require('assert');
var waitingCount = 2;


MongoClient.connect("mongodb://localhost:27017/nearby", function(err, db){
	assert.equal(err, null);
	createEventsTable(db);
	createUsersTable(db);
});


/*
	EVENTS COLLECTION:
	user_id: ObjectId
	title: String
	description: String
	location: GeoJson object
*/
function createEventsTable(db){
	db.listCollections({name: "events"}).toArray(function(err, items){
		if(items.length != 1){
			db.createCollection('events', function(err, result){
				assert.equal(err, null);
				console.log("Created events table");
				finished(db);
			});
		}
		else{
			finished(db);
		}
	});
}

/*
	USERS COLLECTON:
	username: String
	password: String
*/
function createUsersTable(db){
	db.listCollections({name: "users"}).toArray(function(err, items){
		if(items.length != 1){
			db.createCollection('users', function(err, result){
				assert.equal(err, null);
				console.log("Created users table");
				result.createIndex({username: 1}, {unique: 1}, function(err, indexName){
					assert.equal(err, null);
					console.log("Created username index");
					finished(db);
				});
			});
		}
		else {
			finished(db);
		}
	});
}

function finished(db){
	waitingCount -= 1;
	if(waitingCount == 0){
		db.close();
	}
}