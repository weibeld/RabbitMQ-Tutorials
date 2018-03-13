#!/usr/bin/env node

// Establish connection to default RabbitMQ server on localhost, send one
// message to the queue, then exit.

var amqp = require('amqplib');
var queue = 'hello'
var msg = 'Hello World!'

amqp.connect().then(function(conn) {
  conn.createChannel().then(function(ch) {
    ch.assertQueue(queue, {durable: false});
    ch.sendToQueue(queue, Buffer.from(msg));
    console.log(" [x] Sent: %s", msg);
  });
  setTimeout(function() { conn.close(); process.exit(0) }, 500);
});
