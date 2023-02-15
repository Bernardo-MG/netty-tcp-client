
package com.bernardomg.example.netty.tcp.client.channel;

import java.util.Optional;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public final class ResponseCatcherChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ResponseCatcherChannelHandler inboundHandler = new ResponseCatcherChannelHandler();

    public ResponseCatcherChannelInitializer() {
        super();
    }

    public final Boolean getReceived() {
        return inboundHandler.getReceived();
    }

    public final Optional<String> getResponse() {
        return inboundHandler.getResponse();
    }

    @Override
    protected final void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline()
            .addLast(inboundHandler);
    }
}
