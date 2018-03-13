#!/usr/bin/env node

// Establish connection to default RabbitMQ server on localhost, listen on
// queue, and print each message that is consumed from the queue.

var amqp = require('amqplib');
var queue = 'hello'

amqp.connect().then(function(conn) {
  conn.createChannel().then(function(ch) {
    ch.assertQueue(queue, {durable: false});
    console.log(" [*] Listening on queue '%s'", queue);
    ch.consume(queue, function(msg) {
      console.log(" [x] Received: %s", msg.content.toString());
    }, {noAck: true});
  });
});
