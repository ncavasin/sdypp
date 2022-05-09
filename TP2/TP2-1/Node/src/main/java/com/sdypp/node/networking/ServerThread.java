package com.sdypp.node.networking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Data
public class ServerThread extends Thread {
    private Socket currentClient;
    private ServerSocket serverSocket;

    public ServerThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void handleIncomingConnections() {
        try {
            this.currentClient = this.serverSocket.accept();
            if (!this.currentClient.isConnected())
                return;
            log.info("As server, Node accepted TCP connection from {}.", this.getCurrentClient().getInetAddress());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.currentClient.getInputStream()));
            System.out.println("As server, Node received -> " + bufferedReader.readLine());

            this.currentClient.close();
            log.info("As server, Node closed TCP connection with {}.", this.getCurrentClient().getInetAddress());
        } catch (IOException e) {
            log.error("Failed to accept incoming connection.");
        }
    }

    @Override
    public void run() {
        handleIncomingConnections();
    }
}
