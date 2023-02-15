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

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;

import com.bernardomg.example.netty.tcp.client.channel.ResponseCatcherChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty based TCP client.
 *
 * @author bernardo.martinezg
 *
 */
@Slf4j
public final class NettyTcpClient implements Client {

    /**
     * Future for the main channel. Allows sending messages and reacting to responses.
     */
    private ChannelFuture        channelFuture;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Boolean              failed         = false;

    /**
     * Host for the server to which this client will connect.
     */
    private final String         host;

    /**
     * Port for the server to which this client will connect.
     */
    private final Integer        port;

    private Boolean              received       = false;

    private Optional<String>     response       = Optional.empty();

    private Boolean              sent           = false;

    /**
     * CLI writer, to print console messages.
     */
    private final PrintWriter    writer;

    public NettyTcpClient(final String hst, final Integer prt, final PrintWriter wrt) {
        super();

        port = Objects.requireNonNull(prt);
        host = Objects.requireNonNull(hst);
        writer = Objects.requireNonNull(wrt);
    }

    @Override
    public final void close() {
        eventLoopGroup.shutdownGracefully();
    }

    @Override
    public final void connect() {
        final Bootstrap b;

        b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);

        // Sets channel initializer which listens for responses
        b.handler(new ResponseCatcherChannelInitializer(this::handleResponse));

        try {
            log.debug("Connecting to {}:{}", host, port);
            channelFuture = b.connect(host, port)
                .sync();
        } catch (final InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }

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
                        failed = true;
                    }
                })
                .addListener(future -> sent = true);

            // while(!channelFuture.isDone());
            // FIXME: This is awful and prone to errors. Handle the futures as they should be handled
            log.trace("Waiting until the request and response are finished");
            while ((!failed) && ((!sent) || (!received))) {
                // Wait until done
                log.trace("Waiting. Sent: {}. Received: {}. Failed: {}", sent, received, failed);
            }
            log.trace("Finished waiting for response");

            if (response.isEmpty()) {
                writer.println("Received no response");
            } else {
                writer.printf("Received response: %s", response.get());
                writer.println();
            }

            log.debug("Successful request");
        } else {
            log.warn("Request failure");
        }
    }

    /**
     * Channel response event listener. Will be sent to the response catcher, and will receive any response.
     *
     * @param rsp
     *            response received
     */
    private final void handleResponse(final String rsp) {
        response = Optional.ofNullable(rsp);
        received = true;
    }

}
