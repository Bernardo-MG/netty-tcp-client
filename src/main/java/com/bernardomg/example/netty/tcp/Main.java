
package com.bernardomg.example.netty.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.netty.tcp.cli.TcpClientMenu;

import picocli.CommandLine;

public class Main {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        final Integer exitCode;

        exitCode = new CommandLine(new TcpClientMenu()).execute(args);

        LOGGER.debug("Exited with code {}", exitCode);

        System.exit(exitCode);
    }

    public Main() {
        super();
    }

}
