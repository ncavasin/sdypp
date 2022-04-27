package com.sdypp.node.shared;

import java.net.ServerSocket;
import java.net.Socket;

public interface Server extends Networking {

    /**
     * Creates a server socket that listens for incoming connections on specified port.
     *
     * @param port The port to listen on.
     */
    void listenAtPort(int port);

    /**
     * Accept a connection from a peer.
     *
     * @return The socket that is used to communicate with the peer.
     */
    Socket acceptIncomingConnection();

    /**
     * Closes the server socket.
     */
    void unlisten();
}
