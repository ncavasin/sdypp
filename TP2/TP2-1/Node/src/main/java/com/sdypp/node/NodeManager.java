package com.sdypp.node;

import com.sdypp.node.node.Node;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NodeManager {

    public static void main(String[] args) {
        int port = 6521;
        String address = "localhost";
        Node node = new Node();
        Node secondNode = new Node();
        node.listenAtPort(port);
        while (true) {
            node.handleIncomingConnections();
//            Thread.sleep(1000);
//            Socket s = secondNode.connect(new InetSocketAddress(address, port));
//            secondNode.send(s, "Hello, world!".getBytes(StandardCharsets.UTF_8));
//            secondNode.disconnect(s);
        }
    }
}
