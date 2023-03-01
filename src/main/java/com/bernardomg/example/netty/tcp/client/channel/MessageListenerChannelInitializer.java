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

import com.bernardomg.example.netty.tcp.client.TransactionListener;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * Initializes the channel with handlers for listening to transactions.
 * <p>
 * It takes care of:
 * <ul>
 * <li>Encoding/decoding messages to/from string</li>
 * <li>Activating Netty logging</li>
 * <li>Adding a {@link MessageListenerChannelHandler}</li>
 * </ul>
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
public final class MessageListenerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandler channelHandler;

    /**
     * Constructs a channel initializer with the received listener.
     *
     * @param listnr
     *            transaction listener
     */
    public MessageListenerChannelInitializer(final TransactionListener listnr) {
        super();

        channelHandler = new MessageListenerChannelHandler(listnr);
    }

    @Override
    protected final void initChannel(final SocketChannel channel) throws Exception {
        channel.pipeline()
            // Transforms message into a string
            .addLast("encoder", new StringEncoder())
            .addLast("decoder", new StringDecoder())
            // Logging handler
            .addLast(new LoggingHandler())
            // Sends any message received by the channel to the listener
            .addLast(channelHandler);
    }

}
