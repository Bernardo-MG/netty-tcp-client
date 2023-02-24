/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2023 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bernardomg.example.netty.tcp.client;

import java.nio.charset.Charset;
import java.util.Objects;

import com.bernardomg.example.netty.tcp.client.channel.MessageListenerChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty based TCP client.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Slf4j
public final class NettyTcpClient implements Client {

    /**
     * Main channel. For sending messages and reacting to responses.
     */
    private Channel                   channel;

    /**
     * Client event loop. Used when closing the client.
     */
    private final EventLoopGroup      eventLoopGroup = new NioEventLoopGroup();

    /**
     * Host for the server to which this client will connect.
     */
    private final String              host;

    /**
     * Transaction listener. Reacts to events during the request.
     */
    private final TransactionListener listener;

    /**
     * Port for the server to which this client will connect.
     */
    private final Integer             port;

    /**
     * Constructs a client for the received host. The transaction listener will react to events when calling the server.
     *
     * @param hst
     *            host for the client to connect
     * @param prt
     *            host port to connect
     * @param lst
     *            transaction listener
     */
    public NettyTcpClient(final String hst, final Integer prt, final TransactionListener lst) {
        super();

        port = Objects.requireNonNull(prt);
        host = Objects.requireNonNull(hst);
        listener = Objects.requireNonNull(lst);
    }

    @Override
    public final void close() {
        log.debug("Closing connection");

        listener.onStop();

        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public final void connect() {
        final Bootstrap     bootstrap;
        final ChannelFuture channelFuture;

        log.debug("Starting connection");

        listener.onStart();

        bootstrap = new Bootstrap();
        bootstrap
            // Registers groups
            .group(eventLoopGroup)
            // Defines channel
            .channel(NioSocketChannel.class)
            // Configuration
            .option(ChannelOption.SO_KEEPALIVE, true)
            // Sets channel initializer which listens for responses
            .handler(new MessageListenerChannelInitializer(this::handleResponse));

        try {
            log.debug("Connecting to {}:{}", host, port);
            channelFuture = bootstrap.connect(host, port)
                .sync();
        } catch (final InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }

        if (channelFuture.isSuccess()) {
            log.debug("Connected correctly to {}:{}", host, port);
        }

        channel = channelFuture.channel();
    }

    @Override
    public final void request() {
        log.debug("Sending empty message");

        // send message to server
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(future -> {
                if (future.isSuccess()) {
                    log.debug("Successful request future");
                    listener.onSend("");
                } else {
                    log.debug("Failed request future");
                }
            });

        log.debug("Sent message");
    }

    @Override
    public final void request(final String message) {
        log.debug("Sending message {}", message);

        // send message to server
        channel.writeAndFlush(Unpooled.wrappedBuffer(message.getBytes(Charset.defaultCharset())))
            .addListener(future -> {
                if (future.isSuccess()) {
                    log.debug("Successful request future");
                    listener.onSend(message);
                } else {
                    log.debug("Failed request future");
                }
            });

        log.debug("Sent message");
    }

    /**
     * Channel response event listener. Will receive any response sent by the server.
     *
     * @param ctx
     *            channel context
     * @param resp
     *            response received
     */
    private final void handleResponse(final ChannelHandlerContext ctx, final String resp) {
        listener.onReceive(resp);
    }

}
