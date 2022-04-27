package com.sdypp.node;

import com.sdypp.node.node.Node;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        while (true) {
            Node n = new Node();
            n.listenAtPort(6521);
            try {
                Socket s = n.acceptIncomingConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                System.out.println(br.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Thread.sleep(1000);
            Socket s = n.connect(new InetSocketAddress("127.0.0.1", 6521));
            n.send(s, "Hello as client!!!!".getBytes(StandardCharsets.UTF_8));
            n.disconnect(s);
        }
    }
}
