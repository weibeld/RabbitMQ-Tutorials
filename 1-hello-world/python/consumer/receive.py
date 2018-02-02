#!/usr/bin/env python

import pika

# Establish connection to RabbitMQ server on localhost
connection = pika.BlockingConnection()
channel = connection.channel()

# Declare queue (create if not exists)
channel.queue_declare(queue='hello')

# Callback function called when a message is received
def callback(ch, method, properties, body):
    print(" [x] Received %r" % body)

# Register callback function with the queue
channel.basic_consume(callback, queue='hello', no_ack=True)

# Wait for messages
print(' [*] Waiting for messages. To exit press CTRL-C')
channel.start_consuming()
