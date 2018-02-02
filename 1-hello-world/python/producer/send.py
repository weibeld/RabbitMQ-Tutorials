#!/usr/bin/env python

import pika

# Establish connection to RabbitMQ server on localhost
connection = pika.BlockingConnection()
channel = connection.channel()

# Declare queue (create if not exists)
channel.queue_declare(queue='hello')

# Send message to queue
msg = "Hello World!"
channel.basic_publish(exchange='', routing_key='hello', body=msg)
print(" [x] Sent '" + msg + "'")

# Close connection
connection.close()
