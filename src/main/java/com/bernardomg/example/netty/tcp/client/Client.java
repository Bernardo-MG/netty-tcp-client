
package com.bernardomg.example.netty.tcp.client;

import io.netty.channel.ChannelFuture;

public interface Client {

    public void shutdown();

    public ChannelFuture startup() throws InterruptedException;

}
