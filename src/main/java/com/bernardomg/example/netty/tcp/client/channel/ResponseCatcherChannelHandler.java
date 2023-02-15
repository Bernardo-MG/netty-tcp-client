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
