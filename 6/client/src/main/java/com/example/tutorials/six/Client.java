package com.example.tutorials.six;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client {

    private final static String REQUEST_QUEUE = "rpc_queue";

    private Connection connection;
    private Channel channel;
    private String replyQueue;

    public Client() throws Exception {
        // Establish connection to RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        // Queue for server responses server (non-durable, exclusive, auto-delete)
        replyQueue = channel.queueDeclare().getQueue();
    }

    public String call(String message) throws Exception {

        // Correlation ID for this call
        String id = UUID.randomUUID().toString();

        // Set message properties
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(id).replyTo(replyQueue).build();

        // Send message to request queue (if it has been already created by the
        // server, if not, the message is lost).
        channel.basicPublish("", REQUEST_QUEUE, props, message.getBytes("UTF-8"));

        // For blocking main thread until getting response from server
        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
      
        // Read response from server 
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (properties.getCorrelationId().equals(id))
                    response.offer(new String(body, "UTF-8"));
            }
        };
        channel.basicConsume(replyQueue, true, consumer);

        // Block until handleDelivery pushes a value in this structure
        return response.take();
    }

    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
