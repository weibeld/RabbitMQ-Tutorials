#!/usr/bin/env node

var amqp = require('amqplib');
var queue = 'hello';
var message = 'Hello World!'

amqp.connect().then(function(conn) {
  conn.createChannel().then(function(ch) {
    ch.assertQueue(queue, {durable: false});
    // Do the following in the promise of assertQueue, because we need to know
    // the name of the automatically named queue (ok.queue).
    ch.assertQueue('', {exclusive: true}).then(function(ok) {
      var id = generateUuid();
      ch.consume(ok.queue, function(msg) {
        if (msg.properties.correlationId == id) {
          console.log(" [.] Received: %s", msg.content.toString());
          setTimeout(function() { conn.close(); process.exit(0) }, 500);
        }
      }, {noAck: true});
      console.log(" [x] Sending: %s", message);
      ch.sendToQueue(queue, Buffer.from(message),
        {correlationId: id, replyTo: ok.queue});
    });
  });
});

function generateUuid() {
  return Math.random().toString() +
         Math.random().toString() +
         Math.random().toString();
}
