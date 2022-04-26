package com.sdypp.node.node;

import com.sdypp.node.shared.Networking;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class Node implements Networking {

    private final InetSocketAddress nodeAddress;

    public Node(InetSocketAddress nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    @Override
    public Socket connect(InetSocketAddress destinationAddress) {
        try {
            return new Socket(destinationAddress.getAddress(), destinationAddress.getPort());
        } catch (IOException e) {
            log.error("Failed to connect with {}.", destinationAddress);
        }
        return null;
    }

    @Override
    public void send(Socket socket, byte[] message) {
        if (!isConnected(socket)) {
            log.error("Failed to send message to {}. Connection is not open.", nodeAddress);
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
    public void disconnect(Socket socket) {
        if (isConnected(socket)) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                log.error("Error disconnecting from {}.", socket.getInetAddress().toString());
            }
        }
    }

    @Override
    public boolean isConnected(Socket socket) {
        return socket.isConnected();
    }
}
