package com.sdypp.node.node;

import com.sdypp.node.shared.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Node extends AbstractServer implements Client {

    @Override
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

    @Override
    public Socket connect(InetSocketAddress destinationAddress) {
        Socket tcpSocket = null;
        try {
            tcpSocket = new Socket();
            tcpSocket.connect(destinationAddress, 500);
            log.info("As client, Node successfully established TCP connection with {}.", tcpSocket.getInetAddress().toString());
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
                log.info("As client, Node successfully closed its TCP connection to {}.", address);
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
