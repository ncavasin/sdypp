package com.sdypp.node.shared;

public interface Server {

    /**
     * Creates a server socket that listens for incoming connections on specified port.
     *
     * @param port The port to listen on.
     */
    void listenAtPort(int port);

    /**
     * Get ready to accept incoming connections.
     */
    void acceptIncomingConnections();

    /**
     * Closes the server socket.
     */
    void unlisten();
}
