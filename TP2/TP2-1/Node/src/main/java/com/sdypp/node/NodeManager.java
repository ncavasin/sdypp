package com.sdypp.node;

import com.sdypp.node.node.Node;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NodeManager {

    public NodeManager() {
        Node n = new Node(new InetSocketAddress("localhost", 9999));
        Socket s = n.connect(new InetSocketAddress("localhost", 9098));
        n.send(s, "Hello".getBytes(StandardCharsets.UTF_8));
        n.disconnect(s);
    }
}
