# RabbitMQ Tutorials

![RabbitMQ Logo](rabbitmq.png)

Implementations of some of the official [RabbitMQ Tutorials](http://www.rabbitmq.com/getstarted.html) in different programming languages.

## RabbitMQ Libraries

There are different ways to connect to a RabbitMQ server in different programming languages (see [overview](http://www.rabbitmq.com/devtools.html)). In the following, we list the libraries that we use for the different programming languages.

### Java

The Java implementations use the RabbitMQ [Java Client Library](http://www.rabbitmq.com/java-client.html), which you can find as

~~~
com.rabbitmq:amqp-client:X.X.X
~~~

on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.rabbitmq%22%20a%3A%22amqp-client%22).

Here is the [Javadoc API documentation](https://rabbitmq.github.io/rabbitmq-java-client/api/current/overview-summary.html).

### Python

The Python implementations use the [pika](https://pika.readthedocs.io/en/latest/) package. You can install it with `pip install pika`.

## Connect to RabbitMQ Server

In every application that uses RabbitMQ, you have to specify the RabbitMQ server to connect to.

In most libraries it is possible to do this with a URI. Such a URI has the following form:

~~~
amqp://username:password@host:port/virtual_host
~~~

If you omit some of the embedded parameters (for example, `port`), then your library likely uses the corresponding default value (for example, the default RabbitMQ port 5672).

So, a full example URI is:

~~~
amqp://daniel:xyz123@rabbitmq.example.com:5672/daniel
~~~

The following shows how to connect to a RabbitMQ server with this URI in the different programming languages.

### Java

~~~java
ConnectionFactory factory = new ConnectionFactory();
factory.setUri("amqp://daniel:xyz123@rabbitmq.example.com:5672/daniel");
Connection connection = factory.newConnection();
~~~

### Python

~~~python
params = pika.URLParameters('amqp://daniel:xyz123@rabbitmq.example.com:5672/daniel')
connection = pika.BlockingConnection(params)
~~~

See documentation [here](http://pika.readthedocs.io/en/latest/examples/using_urlparameters.html).

## Running RabbitMQ Server Locally

It's possible to run a RabbitMQ server on your local machine. For this you have to install it according to [these instructions](http://www.rabbitmq.com/download.html).

On macOS, just use:

~~~bash
brew install rabbitmq
~~~

And make sure that the executable `rabbitmq-server` is in the `PATH`.

The RabbitMQ server uses by default the following connection parameters:

- Username: `guest`
- Password: `guest`
- Host: `localhost`
- Port: `5672`
- Virtual host: `/`

If you don't change these default values, then you can connect from your library to the local RabbitMQ server with some shortcuts.

### Java

~~~java
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");
Connection connection = factory.newConnection();
~~~

That is, just set the host to `localhost` and no other parameters are needed.

### Python

~~~python
connection = pika.BlockingConnection()
~~~

This is equivalent to:

~~~python
connection = pika.BlockingConnection(pika.URLParameters('amqp://guest:guest@localhost:5672/'))
~~~

## Note

The official tutorial implementations can be found in [this repository](https://github.com/rabbitmq/rabbitmq-tutorials).
