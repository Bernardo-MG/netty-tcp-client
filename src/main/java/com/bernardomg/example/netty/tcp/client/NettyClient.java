
package com.bernardomg.example.netty.tcp.client;

import java.io.PrintWriter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class NettyClient implements Client {

    private ChannelFuture                           channelFuture;

    private final ChannelInitializer<SocketChannel> channelInitializer;

    private final EventLoopGroup                    eventLoopGroup = new NioEventLoopGroup();

    private final String                            host;

    private final NettyChannelInboundHandler        inboundHandler;

    private final Integer                           port;

    private Boolean                                 sent           = false;

    private final PrintWriter                       writer;

    public NettyClient(final String hst, final Integer prt, final PrintWriter wrt) {
        super();

        port = prt;
        host = hst;
        writer = wrt;

        inboundHandler = new NettyChannelInboundHandler();
        channelInitializer = new ChannelInitializer<>() {

            @Override
            protected void initChannel(final SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                    .addLast(inboundHandler);
            }

        };
    }

    @Override
    public final void close() {
        // timeout before closing client
        // try {
        // Thread.sleep(5000);
        // } catch (final InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public final void connect() throws InterruptedException {
        final Bootstrap b;

        b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(channelInitializer);

        channelFuture = b.connect(host, port)
            .sync();

        if (channelFuture.isSuccess()) {
            log.debug("Connected correctly");
        }
    }

    @Override
    public final void send(final String message) {

        log.debug("Sending message {}", message);

        // Prints the final result
        writer.println();
        writer.println("------------");
        writer.printf("Sending message %s to %s:%d", message, host, port);
        writer.println();
        writer.println("------------");

        // check the connection is successful
        if (channelFuture.isSuccess()) {
            log.debug("Starting request");

            // send message to server
            channelFuture.channel()
                .writeAndFlush(Unpooled.wrappedBuffer(message.getBytes()))
                .addListener(future -> {
                    if (future.isSuccess()) {
                        log.debug("Successful request future");
                        writer.printf("Sent message: %s", message);
                        writer.println();
                    } else {
                        log.debug("Failed request future");
                        writer.println("Failed sending message");
                    }
                })
                .addListener(future -> sent = true);

            // while(!channelFuture.isDone());
            // FIXME: This is awful and prone to errors. Handle the futures as they should be handled
            log.debug("Waiting until the request and response are finished");
            while ((!sent) || (!inboundHandler.getReceived())) {
                // Wait until done
                log.trace("Waiting. Sent: {}. Received: {}", sent,inboundHandler.getReceived());
            }
            log.debug("Finished waiting for response");

            if (inboundHandler.getResponse()
                .isEmpty()) {
                writer.println("Received no response");
            } else {
                writer.printf("Received Message: %s", inboundHandler.getResponse()
                    .get());
                writer.println();
            }

            log.debug("Successful request");
        } else {
            log.warn("Request failure");
        }
    }

}
