package com.sdypp.node.shared;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This interface defines the methods that a peer must implement in order to communicate with other peers.
 */
public interface Networking {

    /**
     * Connect to a peer on the specified InetSocketAddress.
     *
     * @param destination The address of the peer to connect to.
     * @return The socket that is used to communicate with the peer.
     */
    Socket connect(InetSocketAddress destination);

    /**
     * Send a message to a peer.
     *
     * @param socket  The socket that is used to communicate with the peer.
     * @param message The message to send.
     */
    void send(Socket socket, byte[] message);

    /**
     * Disconnect from a peer and close the socket.
     *
     * @param socket The socket that must be closed.
     */
    void disconnect(Socket socket);

    /**
     * Broadcast a message to all peers belonging to the same multicast address group.
     *
     * @param multicastAddress The multicast address to use.
     * @param message          The message to broadcast.
     */
    void multicast(InetSocketAddress multicastAddress, byte[] message);

    /**
     * Checks if the connection is still alive.
     *
     * @param socket The socket to check.
     * @return True if the connection is still alive, false otherwise.
     */
    boolean isConnected(Socket socket);
}
