#!/usr/bin/env python

import pika
import uuid

class RpcClient(object):
    """
    An RPC client for RPC calls with a single argument.
    """

    req_queue_name = "rpc_requests"

    def __init__(self):
        """
        Create an RPC client for single-argument RPC calls.
        """
        # Establish connection to RabbitMQ server
        self.connection = pika.BlockingConnection()
        self.channel = self.connection.channel()

        # Queue for requests from client to server
        self.req_queue = self.channel.queue_declare(self.req_queue_name)

        # Queue for responses from server (auto-named)
        self.res_queue = self.channel.queue_declare(exclusive=True).method.queue

        # Register callback method for handling messages in response queue
        self.channel.basic_consume(self.on_response, no_ack=True,
                                   queue=self.res_queue)


    def on_response(self, channel, method, props, body):
        """
        Callback method handling messages in the response queue.
        """
        if self.correlation_id == props.correlation_id:
            self.response = body


    def call(self, arg):
        """
        Perform RPC call with a single argument.
        """
        # Note: it's safe to use instance variables for response and corr ID,
        # because the RPC client object can perform only a single RPC call at
        # (a new call can only be performed when the previous one finished).
        self.response = None
        self.correlation_id = str(uuid.uuid4())

        # Send message to server
        props = pika.BasicProperties(reply_to=self.res_queue,
                                     correlation_id=self.correlation_id)
        self.channel.basic_publish(exchange='',
                                   routing_key=self.req_queue_name,
                                   properties=props,
                                   body=str(arg))

        # Block until the response message has been read from response queue
        while self.response is None:
            self.connection.process_data_events()

        return self.response

# Main program
rpc_client = RpcClient()

arg = 5
print(" [x] Requesting fib(" + str(arg) + ")")
response = rpc_client.call(arg)
print(" [.] Got %r" % response)

arg = 6
print(" [x] Requesting fib(" + str(arg) + ")")
response = rpc_client.call(arg)
print(" [.] Got %r" % response)
