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

import java.io.OutputStream;
import java.io.PrintWriter;

import com.bernardomg.example.netty.tcp.cli.CliWriterTransactionListener;
import com.bernardomg.example.netty.tcp.cli.version.ManifestVersionProvider;
import com.bernardomg.example.netty.tcp.client.Client;
import com.bernardomg.example.netty.tcp.client.NettyTcpClient;
import com.bernardomg.example.netty.tcp.client.TransactionListener;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * Send empty message command. Will send an empty message to the server through TCP.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 *
 */
@Command(name = "empty", description = "Sends an empty TCP message", mixinStandardHelpOptions = true,
        versionProvider = ManifestVersionProvider.class)
public final class SendEmptyMessageCommand implements Runnable {

    /**
     * Server host.
     */
    @Parameters(index = "0", description = "Server host", paramLabel = "HOST")
    private String      host;

    /**
     * Server port.
     */
    @Parameters(index = "1", description = "Server port", paramLabel = "PORT")
    private Integer     port;

    /**
     * Command specification. Used to get the line output.
     */
    @Spec
    private CommandSpec spec;

    /**
     * Verbose mode. If active prints info into the console. Active by default.
     */
    @Option(names = { "--verbose" }, paramLabel = "VERBOSE", description = "print information to console",
            defaultValue = "true")
    private Boolean     verbose;

    /**
     * Default constructor.
     */
    public SendEmptyMessageCommand() {
        super();
    }

    @Override
    public final void run() {
        final PrintWriter         writer;
        final Client              client;
        final TransactionListener listener;

        if (verbose) {
            // Prints to console
            writer = spec.commandLine()
                .getOut();
        } else {
            // Prints nothing
            writer = new PrintWriter(OutputStream.nullOutputStream());
        }

        // Create client
        listener = new CliWriterTransactionListener(host, port, writer);
        client = new NettyTcpClient(host, port, listener);
        client.connect();

        // Send message
        client.request();

        // close client
        client.close();
    }

}