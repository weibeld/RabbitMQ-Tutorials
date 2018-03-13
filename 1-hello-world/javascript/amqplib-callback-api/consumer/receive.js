#!/usr/bin/env node

var amqp = require('amqplib/callback_api');

var uri = 'amqp://localhost'
var queueName = 'hello'

// Establish connection to RabbitMQ server
amqp.connect(uri, function(err, conn) {
  conn.createChannel(function(err, ch) {
    // Declare queue (create if not exists)
    ch.assertQueue(queueName, {durable: false});
    // Consume messages from queue
    console.log(" [*] Waiting for messages in %s.", queueName);
    ch.consume(queueName, function(msg) {
      // Callback method for handling messages from queue
      console.log(" [x] Received %s", msg.content.toString());
    }, {noAck: true});
  });
});
