package com.sdypp.node.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Peer2PeerServer implements Server {
    private ServerSocket serverSocket;

    private Socket currentClient;

    private BufferedReader bufferedReader;

    private BufferedWriter bufferedWriter;

    public void listenAtPort(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            log.info("As server, Node is listening for TCP connections at port {}.", port);
        } catch (IOException e) {
            log.error("Failed to listen TCP connections at port {}.", port);
        }
        this.setServerSocket(serverSocket);
    }

    @Override
    public void acceptIncomingConnections() {
        try {
            if (this.getCurrentClient() == null) {
                this.setCurrentClient(this.getServerSocket().accept());
                log.info("As server, Node accepted TCP connection from {}.", this.getCurrentClient().getInetAddress());
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getCurrentClient().getInputStream()));
            System.out.println("As server, Node received -> " + bufferedReader.readLine());
        } catch (IOException e) {
            log.error("Failed to accept incoming connection.");
        }
    }

    @Override
    public void unlisten() {
        try {
            this.getCurrentClient().close();
            this.setCurrentClient(null);
            this.getServerSocket().close();
            this.setServerSocket(null);
            log.info("As server, Node's TCP ServerSocket successfully closed.");
        } catch (IOException e) {
            log.error("Failed to close TCP ServerSocket.");
        }
    }
}
