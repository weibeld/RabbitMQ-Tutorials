#!/usr/bin/env python

import pika

req_queue_name = "rpc_requests"

# Establish connection to RabbitMQ server
connection = pika.BlockingConnection()
channel = connection.channel()

# Queue for requests from client to server
req_queue = channel.queue_declare(req_queue_name)

# Function exposed over RPC
def fib(n):
    if n == 0:
        return 0
    elif n == 1:
        return 1
    else:
        return fib(n-1) + fib(n-2)

# Callback function for handling message in the request queue
def on_request(channel, method, props, body):
    arg = int(body)
    print(" [.] fib(%s)" % arg)

    response = str(fib(arg))

    print(" [ ] Sending back '%s'" % response)
    res_props = pika.BasicProperties(correlation_id = props.correlation_id)
    channel.basic_publish(exchange='',
                          routing_key=props.reply_to,
                          properties=res_props,
                          body=response)

    channel.basic_ack(delivery_tag=method.delivery_tag)

# Register callback function for handling messages in request queue
channel.basic_qos(prefetch_count=1)
channel.basic_consume(on_request, queue=req_queue_name)

# Wait for and handle RPC requests
print(" [x] Awaiting RPC requests")
channel.start_consuming()
