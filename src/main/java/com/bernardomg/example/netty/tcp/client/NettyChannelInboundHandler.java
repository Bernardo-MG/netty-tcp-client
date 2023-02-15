
package com.bernardomg.example.netty.tcp.client;

import java.nio.charset.Charset;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class NettyChannelInboundHandler extends SimpleChannelInboundHandler<Object> {

    private Boolean          received = false;

    private Optional<String> response = Optional.empty();

    public NettyChannelInboundHandler() {
        super();
    }

    @Override
    public final void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf byteBuf;
        final String  message;

        byteBuf = (ByteBuf) msg;
        message = byteBuf.toString(Charset.defaultCharset());
        response = Optional.ofNullable(message);

        received = true;
    }

    public final Boolean getReceived() {
        return received;
    }

    public final Optional<String> getResponse() {
        return response;
    }

}
