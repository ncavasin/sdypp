package com.sdypp.master.connection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class ConnectionHandler {
    private final Socket connection;

    public void accept(){
        ConnectionHandlerInner connectionHandlerInner = new ConnectionHandlerInner(connection);
        Thread t = new Thread(connectionHandlerInner);
        t.start();
    }

    private record ConnectionHandlerInner(Socket connection) implements Runnable {

        @Override
        public void run() {

        }
    }
}
