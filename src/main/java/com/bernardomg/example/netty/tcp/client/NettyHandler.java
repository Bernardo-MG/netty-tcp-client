
package com.bernardomg.example.netty.tcp.client;

import java.io.PrintWriter;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyHandler extends SimpleChannelInboundHandler<Object> {

    private final PrintWriter writer;

    public NettyHandler(final PrintWriter wrt) {
        super();

        writer = wrt;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        final String  message = byteBuf.toString(Charset.defaultCharset());
        writer.printf("Received Message: %s", message);
        writer.println();
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

}
