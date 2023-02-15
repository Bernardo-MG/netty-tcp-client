
package com.bernardomg.example.netty.tcp.client.channel;

import java.nio.charset.Charset;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ResponseCatcherChannelHandler extends SimpleChannelInboundHandler<Object> {

    private Boolean          received = false;

    private Optional<String> response = Optional.empty();

    public ResponseCatcherChannelHandler() {
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

        log.debug("Received message {}", response.get());
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        log.error(cause.getLocalizedMessage(), cause);
    }

    public final Boolean getReceived() {
        return received;
    }

    public final Optional<String> getResponse() {
        return response;
    }

}
