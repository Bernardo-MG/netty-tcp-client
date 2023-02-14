
package com.bernardomg.example.netty.tcp;

import com.bernardomg.example.netty.tcp.cli.TcpClientMenu;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
public class Main {

    public static void main(final String[] args) {
        final Integer exitCode;

        exitCode = new CommandLine(new TcpClientMenu()).execute(args);

        log.debug("Exited with code {}", exitCode);

        System.exit(exitCode);
    }

    public Main() {
        super();
    }

}
