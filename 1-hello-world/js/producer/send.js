#!/usr/bin/env node

var amqp = require('amqplib/callback_api');

var uri = 'amqp://localhost'
var queueName = 'hello'
var msg = 'Hello World!'

// Establish connection to RabbitMQ server
amqp.connect(uri, function(err, conn) {
  conn.createChannel(function(err, ch) {
    // Declare queue (create if not exists)
    ch.assertQueue(queueName, {durable: false});
    // Send message to queue
    ch.sendToQueue(queueName, Buffer.from(msg));
    console.log(" [x] Sent '" + msg + "'");
  });
  // Close connection
  setTimeout(function() { conn.close(); process.exit(0) }, 500);
});
