package com.sdypp.node;

import com.sdypp.node.node.Node;

import java.net.InetSocketAddress;

public class NodeManager {

    public NodeManager() {
        Node n = new Node(new InetSocketAddress("localhost", 9999));
        n.connect(new InetSocketAddress("localhost", 9098));
        n.send();
        n.disconnect();
    }
}
