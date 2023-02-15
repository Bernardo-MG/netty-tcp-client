
package com.bernardomg.example.netty.tcp.client.channel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public final class ResponseCatcherChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ResponseCatcherChannelHandler inboundHandler;

    public ResponseCatcherChannelInitializer(final Consumer<String> listener) {
        super();

        inboundHandler = new ResponseCatcherChannelHandler(listener);
    }

    @Override
    protected final void initChannel(final SocketChannel ch) throws Exception {
        ch.pipeline()
            .addLast(inboundHandler);
    }

}
