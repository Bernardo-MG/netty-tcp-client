
package com.bernardomg.example.netty.tcp.cli;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;

import com.bernardomg.example.netty.tcp.client.ClientListener;

public final class CliWriterClientListener implements ClientListener {

    /**
     * Host for the server to which this client will connect.
     */
    private final String      host;

    /**
     * Port for the server to which this client will connect.
     */
    private final Integer     port;

    /**
     * CLI writer, to print console messages.
     */
    private final PrintWriter writer;

    public CliWriterClientListener(final String hst, final Integer prt, final PrintWriter wrt) {
        super();

        port = Objects.requireNonNull(prt);
        host = Objects.requireNonNull(hst);
        writer = Objects.requireNonNull(wrt);
    }

    @Override
    public final void onClose() {
        writer.println("------------");
        writer.println("Closing connection");
        writer.println("------------");
    }

    @Override
    public final void onConnect() {
        writer.println("------------");
        writer.printf("Opening connection to %s:%d", host, port);
        writer.println();
        writer.println("------------");
    }

    @Override
    public final void onRequest(final String request, final Optional<String> response, final Boolean success) {
        // Prints the final result
        writer.println();
        writer.println("------------");
        writer.printf("Sending message %s", request);
        writer.println();
        writer.println("------------");

        if (success) {
            writer.println("Sent message successfully");
        } else {
            writer.println("Failed sending message");
        }

        if (response.isEmpty()) {
            writer.println("Received no response");
        } else {
            writer.printf("Received response: %s", response.get());
            writer.println();
        }
    }

}
