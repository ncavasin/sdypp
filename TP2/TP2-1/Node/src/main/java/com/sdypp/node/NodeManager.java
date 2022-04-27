package com.sdypp.node;

import com.sdypp.node.node.Node;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NodeManager {

    public NodeManager() {
        Node n = new Node();
        Socket socket = n.connect(new InetSocketAddress("localhost", 9098));
        n.send(socket, "Hello".getBytes(StandardCharsets.UTF_8));
        n.multicast(new InetSocketAddress("localhost", 9098), "Hello".getBytes(StandardCharsets.UTF_8));
        n.disconnect(socket);
    }
}
