package com.sdypp.node.shared;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This interface defines the methods that a peer must implement in order to communicate with other peers.
 */
public interface Networking {

    /**
     * Connect to a peer.
     */
    Socket connect(InetSocketAddress destination);

    /**
     * Send a message to a peer.
     */
    void send(Socket socket, byte[] message);

    /**
     * Disconnect from a peer.
     */
    void disconnect(Socket socket);

    /**
     * This method is used to check if the connection is still alive.
     */
    boolean isConnected(Socket socket);
}
