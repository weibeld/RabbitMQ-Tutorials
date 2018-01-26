package com.example.tutorials.six;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;
import java.io.IOException;

public class Server {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws Exception {

        // Establish connection to RabbitMQ server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        // Create queue on which to receive RPC calls
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);
        System.out.println(" [x] Awaiting RPC requests");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties requestProps, byte[] body) throws IOException {

                // Read message from client
                int n = -1;
                try {
                    String message = new String(body,"UTF-8");
                    n = Integer.parseInt(message);
                } catch (Exception e) {
                    System.out.println(" [.] " + e.toString());
                }

                // Calculate response
                System.out.println(" [.] fib(" + n + ")");
                String response = fib(n) + "";
        
                // Construct response message
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(requestProps.getCorrelationId())
                    .build();

                // Send response message
                channel.basicPublish("", requestProps.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);

                synchronized(this) {
                    this.notify();
                }
            }
        };
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
    }

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n-1) + fib(n-2);
    }
}
