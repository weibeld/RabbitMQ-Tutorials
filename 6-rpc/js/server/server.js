#!/usr/bin/env node

var amqp = require('amqplib/callback_api');

var uri = 'amqp://localhost'
var queueName = 'rpc_queue'

// Establish connection to RabbitMQ server
amqp.connect(uri, function(err, conn) {
  conn.createChannel(function(err, ch) {
    // Declare queue for RPC requests (create if not exists)
    ch.assertQueue(queueName, {durable: false});
    // Ensure messages in queue are spread equally over servers
    ch.prefetch(1);
    // Wait for messages from client
    console.log(' [x] Awaiting RPC requests');
    ch.consume(queueName, function reply(msg) {
      // Callback function for handling messages from client
      var n = parseInt(msg.content.toString());
      console.log(" [.] fib(%d)", n);
      var r = fibonacci(n);
      console.log(" [ ] Sending back %d", r);
      // Send message with result back to client 
      ch.sendToQueue(msg.properties.replyTo,
                     new Buffer(r.toString()),
                     {correlationId: msg.properties.correlationId});
      ch.ack(msg);
    });
  });
});

function fibonacci(n) {
  if (n == 0 || n == 1)
    return n;
  else
    return fibonacci(n - 1) + fibonacci(n - 2);
}
