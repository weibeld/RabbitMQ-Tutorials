#!/usr/bin/env node

// Connect to RabbitMQ server on localhost, listen on queue. For each consumed 
// message, send a reply back to the queue indicated within the message.

var amqp = require('amqplib');
var queue = 'hello';
var reply = 'Hi back';

amqp.connect().then(function(conn) {
  conn.createChannel().then(function(ch) {
    ch.assertQueue(queue, {durable: false});
    ch.prefetch(1);
    console.log(' [x] Awaiting RPC requests');
    ch.consume(queue, function(msg) {
      console.log(" [.] Receiving: %s", msg.content.toString());
      console.log(" [ ] Sending back: %s", reply);
      ch.sendToQueue(msg.properties.replyTo, Buffer.from(reply),
        {correlationId: msg.properties.correlationId});
      ch.ack(msg);
    });
  });
});
