package com.example.tutorials.one;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Send {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        // Establish connection to server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Bind to queue (create if not exists)
        // Queue: - non-durable (does not survive server restart)
        //        - non-exclusive (can be used by other connections)
        //        - non auto-delete (server doesn't delete when not in use)
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // Send message to queue
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // Close connection
        channel.close();
        connection.close();
    }
}
