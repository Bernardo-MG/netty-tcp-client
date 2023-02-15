
package com.bernardomg.example.netty.tcp.client.channel;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ResponseCatcherChannelHandler extends SimpleChannelInboundHandler<Object> {

    private final Consumer<String> responseListener;

    public ResponseCatcherChannelHandler(final Consumer<String> listener) {
        super();

        responseListener = listener;
    }

    @Override
    public final void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf byteBuf;
        final String  message;

        byteBuf = (ByteBuf) msg;
        message = byteBuf.toString(Charset.defaultCharset());
        log.debug("Received message {}", message);

        responseListener.accept(message);
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        log.error(cause.getLocalizedMessage(), cause);
    }

}
