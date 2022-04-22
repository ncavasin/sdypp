package com.sdypp.node.node;

import com.sdypp.node.shared.Networking;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class Node implements Networking {

    private final InetSocketAddress nodeSocket;
    private InetSocketAddress destination;
    private Socket socket;

    public Node(InetSocketAddress nodeSocket) {
        this.nodeSocket = nodeSocket;
        socket = null;
    }

    @Override
    public void connect(InetSocketAddress destination) {
        this.destination = destination;
        try {
            socket = new Socket(destination.getAddress(), destination.getPort());
        } catch (IOException e) {
            log.error("Failed to connect with {}", destination);
        }
    }

    @Override
    public void send() {
        if (!isConnected()) {
            log.error("Failed to send message to {}. Connection is not open.", nodeSocket);
            return;
        }

        try {
            OutputStream os = socket.getOutputStream();
            os.write(new byte[1024]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (!isConnected()) {
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }
}
