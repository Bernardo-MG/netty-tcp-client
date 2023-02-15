
package com.bernardomg.example.netty.tcp.client;

public interface Client {

    public void send(final String message);

    public void shutdown();

    public void startup() throws InterruptedException;

}
