
package com.bernardomg.example.netty.tcp.client;

import java.io.PrintWriter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class NettyClient implements Client {

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private final String         host;

    private final Integer        port;

    private final PrintWriter    writer;

    /**
     * Constructor
     *
     * @param hst
     *            server host
     * @param prt
     *            server port
     */
    public NettyClient(final String hst, final Integer prt, final PrintWriter wrt) {
        super();

        port = prt;
        host = hst;
        writer = wrt;
    }

    /**
     * Shutdown a client
     */
    @Override
    public final void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * Startup the client
     *
     * @return {@link ChannelFuture}
     * @throws InterruptedException
     */
    @Override
    public final ChannelFuture startup() throws InterruptedException {
        final ChannelInitializer<SocketChannel> channelInitializer;
        final Bootstrap                         b;

        channelInitializer = new ChannelInitializer<>() {

            @Override
            protected void initChannel(final SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                    .addLast(new NettyHandler(writer));
            }

        };

        b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(channelInitializer);

        return b.connect(host, port)
            .sync();
    }

}
