package com.sdypp.node.shared;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * This interface represents a peer in the network. It defines all the properties
 * and methods that a peer must implement to be able to communicate with other
 */
public interface Peer {

    /**
     * Get the peer's id.
     * @return timestamp
     */
    String getId();
    void setId(LocalDateTime time);

    InetAddress getAddress();
    void setAddress(InetAddress address);

    int getPort();
    void setPort(int port);
}
