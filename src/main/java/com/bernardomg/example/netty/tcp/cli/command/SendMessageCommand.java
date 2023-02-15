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

package com.bernardomg.example.netty.tcp.cli.command;

import java.io.PrintWriter;

import com.bernardomg.example.netty.tcp.cli.version.ManifestVersionProvider;
import com.bernardomg.example.netty.tcp.client.Client;
import com.bernardomg.example.netty.tcp.client.NettyTcpClient;

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
        final PrintWriter writer;
        final Client      client;

        writer = spec.commandLine()
            .getOut();

        try {
            // Create client
            client = new NettyTcpClient(host, port, writer);
            client.connect();

            // Send message
            client.send(message);

            // close client
            client.close();
        } catch (final Exception e) {
            e.printStackTrace();
            writer.println("Error on startup");
        }
    }

}
