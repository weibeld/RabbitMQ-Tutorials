package com.example.tutorials.two;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.BuiltinExchangeType;

public class EmitLog {

    private final static String EX_NAME = "logs";

    public static void main(String[] args) throws Exception {

        // Establish connection to server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare exchange (create if not exists)
        channel.exchangeDeclare(EX_NAME, BuiltinExchangeType.FANOUT);
        
        // Send message to exchange
        String message = args.length < 1 ? "Hello World!" : args[0];
        channel.basicPublish(EX_NAME, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        // Terminate connection
        channel.close();
        connection.close();
    }

}

