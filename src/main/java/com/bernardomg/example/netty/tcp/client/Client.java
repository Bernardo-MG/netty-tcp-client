
package com.bernardomg.example.netty.tcp.client;

public interface Client {

    public void close();

    public void connect() throws InterruptedException;

    public void send(final String message);

}
