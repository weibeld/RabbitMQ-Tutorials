package com.example.tutorials.six;

public class Main {

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        System.out.println(" [x] Requesting fib(10)");
        String response = client.call("10");
        System.out.println(" [.] Got '" + response + "'");

        client.close();
    }

}
