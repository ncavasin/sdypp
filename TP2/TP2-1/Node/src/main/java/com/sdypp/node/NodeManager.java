package com.sdypp.node;

import com.sdypp.node.node.Node;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class NodeManager {

    public NodeManager() {
        Node n = new Node();
        Socket socket = n.connect(new InetSocketAddress("localhost", 9098));

        n.send(socket, "Hello".getBytes(StandardCharsets.UTF_8));
        n.multicast(new InetSocketAddress("localhost", 9098), "Hello".getBytes(StandardCharsets.UTF_8));
        n.disconnect(socket);
    }

    public static void main(String[] args) throws InterruptedException {
//        new NodeManager();
        int port = 6521;
        String address = "localhost";
        Node node = new Node();
        node.listenAtPort(port);
        while (true) {
            new Thread(node::acceptIncomingConnections).start();
            Thread.sleep(1000);
            Socket s = node.connect(new InetSocketAddress(address, port));
            node.send(s, "Hello from client!!!!".getBytes(StandardCharsets.UTF_8));
            node.disconnect(s);
        }
    }
}
