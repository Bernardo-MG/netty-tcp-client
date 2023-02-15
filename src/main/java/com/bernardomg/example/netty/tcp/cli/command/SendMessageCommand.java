/**
 * Copyright 2020-2022 the original author or authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.bernardomg.example.netty.tcp.cli.command;

import java.io.PrintWriter;

import com.bernardomg.example.netty.tcp.cli.version.ManifestVersionProvider;
import com.bernardomg.example.netty.tcp.client.Client;
import com.bernardomg.example.netty.tcp.client.NettyClient;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * Dice gatherer command. Receives an expression, gets all the dice sets on it and prints the result on screen.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Command(name = "message", description = "Sends a TCP message", mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class)
@Slf4j
public final class SendMessageCommand implements Runnable {

    @Parameters(index = "0", description = "Server host", paramLabel = "HOST")
    private String      host;

    @Parameters(index = "2", description = "Message to send", paramLabel = "MSG")
    private String      message;

    @Parameters(index = "1", description = "Server port", paramLabel = "PORT")
    private Integer     port;

    /**
     * Command specification. Used to get the line output.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Default constructor.
     */
    public SendMessageCommand() {
        super();
    }

    @Override
    public final void run() {
        final PrintWriter   writer;
        final Client        client;
        final ChannelFuture channelFuture;

        writer = spec.commandLine()
            .getOut();

        // Prints the final result
        writer.println();
        writer.println("------------");
        writer.printf("Sending message %s to %s:%d", message, host, port);
        writer.println();
        writer.println("------------");

        try {
            // Create a client
            client = new NettyClient(host, port, writer);
            channelFuture = client.startup();

            // wait for 5 seconds
            Thread.sleep(5000);
            // check the connection is successful
            if (channelFuture.isSuccess()) {
                log.debug("Successful request");

                // send message to server
                channelFuture.channel()
                    .writeAndFlush(Unpooled.wrappedBuffer(message.getBytes()))
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            writer.printf("Sent message: %s", message);
                            writer.println();
                        } else {
                            writer.println("Failed sending message");
                        }
                    });
            }
            // timeout before closing client
            Thread.sleep(5000);

            // close the client
            client.shutdown();
        } catch (final Exception e) {
            e.printStackTrace();
            writer.println("Error on startup");
        }
    }

}
