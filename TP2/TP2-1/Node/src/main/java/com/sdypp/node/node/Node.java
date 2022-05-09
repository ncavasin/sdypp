package com.sdypp.node.node;

import com.sdypp.node.networking.P2PClient;
import com.sdypp.node.networking.P2PServer;
import com.sdypp.node.networking.ServerThread;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.Semaphore;

@Data
@Slf4j
@NoArgsConstructor
public class Node implements P2PClient, P2PServer {
    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private int THREAD_POOL_SIZE = 10;
    private Semaphore semaphore = new Semaphore(THREAD_POOL_SIZE);

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
    public void handleIncomingConnections() {
        try {
            this.semaphore.acquire();
            new ServerThread(this.serverSocket).start();
        } catch (InterruptedException e) {
            log.error("Failed to acquire semaphore. Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to start new ServerThread. Error: {}", e.getMessage());
        } finally {
            this.semaphore.release();
        }
    }

    @Override
    public void unlisten() {
        try {
            this.getServerSocket().close();
            this.setServerSocket(null);
            log.info("As server, Node's TCP ServerSocket successfully closed.");
        } catch (IOException e) {
            log.error("Failed to close TCP ServerSocket.");
        }
    }

    @Override
    public Socket connect(InetSocketAddress destinationAddress) {
        Socket tcpSocket = null;
        try {
            tcpSocket = new Socket();
            tcpSocket.connect(destinationAddress, 500);
            log.info("As client, Node connected with {} using TCP.", tcpSocket.getInetAddress().toString());
        } catch (IOException e) {
            log.error("Failed to connect with {}.", destinationAddress);
        }
        return tcpSocket;
    }

    @Override
    public void send(Socket tcpSocket, byte[] message) {
        String address = tcpSocket.getInetAddress().toString();
        if (!isConnected(tcpSocket)) {
            log.error("As client, Node failed to send message to {} using TCP. There's no such connection!", address);
            return;
        }

        try {
            OutputStream os = tcpSocket.getOutputStream();
            os.write(message);
            log.info("As client, Node successfully sent a message to {} using TCP.", address);
        } catch (IOException e) {
            log.error("Failed to send message to {}.", address);
        }
    }

    @Override
    public void disconnect(Socket tcpSocket) {
        String address = tcpSocket.getInetAddress().toString();
        if (isConnected(tcpSocket)) {
            try {
                tcpSocket.close();
                log.info("As client, Node disconnected from {}.", address);
            } catch (IOException e) {
                log.error("Error disconnecting from {}.", address);
            }
        }
    }

    @Override
    public void multicast(InetSocketAddress multicastAddress, byte[] message) {
        try (DatagramSocket udpSocket = new DatagramSocket(multicastAddress)) {
            try {
                DatagramPacket datagram = new DatagramPacket(message, message.length, multicastAddress);
                datagram.setData(message, 0, message.length);
                udpSocket.send(datagram);
                log.info("Multicast message successfully sent to {} using UDP.", multicastAddress);
            } catch (IOException e) {
                log.error("Failed to multicast message to {}.", udpSocket.getInetAddress().toString());
            }
        } catch (SocketException e) {
            log.error("Failed to create multicast UDP Socket for address {}.", multicastAddress);
        }
    }

    @Override
    public boolean isConnected(Socket tcpSocket) {
        return tcpSocket.isConnected();
    }

}
