#!/usr/bin/env node

var amqp = require('amqplib/callback_api');

var uri = 'amqp://localhost'
var queueName = 'rpc_queue'

// Establish connection to RabbitMQ server
amqp.connect(uri, function(err, conn) {
  conn.createChannel(function(err, ch) {
    // Declare queue for RPC requests (create if not exists)
    ch.assertQueue(queueName, {durable: false});
    // Declare anonymous queue for responses from server
    ch.assertQueue('', {exclusive: true}, function(err, q) {
      var correlationId = generateUuid();
      var num = 10;
      console.log(' [x] Requesting fib(%d)', num);
      // Register callback function for handling response from server
      ch.consume(queueName.queue, function(msg) {
          if (msg.properties.correlationId == correlationId) {
            console.log(' [.] Got %s', msg.content.toString());
            setTimeout(function() { conn.close(); process.exit(0) }, 500);
          }
        }, {noAck: true}); 
        // Send request to server
        ch.sendToQueue(queueName,
                       new Buffer(num.toString()),
                       { correlationId: correlationId, replyTo: q.queue });
    });
  });
});

function generateUuid() {
  return Math.random().toString() +
         Math.random().toString() +
         Math.random().toString();
}
