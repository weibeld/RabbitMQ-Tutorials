package com.example.tutorials.two;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import java.io.IOException;


// Subscribe to the messages published by the producer through the defined
// exchange. This means creating a queue and binding it to the exchange. Many
// ReceiveLogs processes can run in parallel.
public class ReceiveLogs {

     private final static String EX_NAME = "logs";
 
     public static void main(String[] args) throws Exception {
    
        // Establish connection to server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare exchange (create if not exists)
        channel.exchangeDeclare(EX_NAME, BuiltinExchangeType.FANOUT);
 
        // Create server-named queue that is non-durable, exclusive, auto-delete
        // Sames as: queueDeclare("", false, true, true, null)
        String queueName = channel.queueDeclare().getQueue();

        // Bind queue to exchange
        channel.queueBind(queueName, EX_NAME, "");

        // Define consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        channel.basicConsume(queueName, true, consumer);
    }
}
