package com.sdypp.node.networking;

import org.springframework.stereotype.Service;

@Service
public interface P2PServer {

    /**
     * Creates a server socket that listens for incoming connections on specified port.
     *
     * @param port The port to listen on.
     */
    void listenAtPort(int port);

    /**
     * Handles incoming connections in a separate thread.
     */
    void handleIncomingConnections();

    /**
     * Closes the server socket.
     */
    void unlisten();
}
