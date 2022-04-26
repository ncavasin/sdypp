package com.sdypp.node.shared;

import java.net.InetSocketAddress;

/**
 * This interface defines the methods that a peer must implement in order to communicate with other peers.
 */
public interface Networking {

    /**
     * Connect to a peer.
     */
    void connect(InetSocketAddress destination);

    /**
     * Send a message to a peer.
     */
    void send();

    /**
     * Disconnect from a peer.
     */
    void disconnect();

    /**
     * This method is used to check if the connection is still alive.
     */
    boolean isConnected();
}
