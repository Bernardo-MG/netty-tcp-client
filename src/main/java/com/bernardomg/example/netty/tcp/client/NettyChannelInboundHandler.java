
package com.bernardomg.example.netty.tcp.client;

import java.io.PrintWriter;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class NettyChannelInboundHandler extends SimpleChannelInboundHandler<Object> {

    private Boolean           received = false;

    private final PrintWriter writer;

    public NettyChannelInboundHandler(final PrintWriter wrt) {
        super();

        writer = wrt;
    }

    @Override
    public final void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf byteBuf;
        final String  message;

        byteBuf = (ByteBuf) msg;
        message = byteBuf.toString(Charset.defaultCharset());

        writer.printf("Received Message: %s", message);
        writer.println();

        received = true;
    }

    public Boolean getReceived() {
        return received;
    }

}
