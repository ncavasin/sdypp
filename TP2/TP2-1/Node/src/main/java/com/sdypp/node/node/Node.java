package com.sdypp.node.node;

import com.sdypp.node.shared.Networking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class Node extends AbstractServer implements Networking {

    @Override
    public void listenAtPort(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            log.info("TCP ServerSocket at port {} successfully created.", port);
        } catch (IOException e) {
            log.error("Failed to listen at port {}.", port);
        }
        this.setServerSocket(serverSocket);
    }

    @Override
    public Socket acceptIncomingConnection() {
        Socket incomingConnection = null;
        try {
            incomingConnection = this.getServerSocket().accept();
            log.info("TCP ServerSocket accepted connection at port {} successfully created.", incomingConnection.getInetAddress());
        } catch (IOException e) {
            log.error("Failed to accept incoming connection.");
        }
        return incomingConnection;
    }

    @Override
    public void unlisten() {
        try {
            this.getServerSocket().close();
            log.info("TCP ServerSocket successfully closed.");
        } catch (IOException e) {
            log.error("Failed to close TCP ServerSocket.");
        }
    }

    @Override
    public Socket connect(InetSocketAddress destinationAddress) {
        Socket tcpSocket = null;
        try {
            tcpSocket = new Socket(destinationAddress.getAddress(), destinationAddress.getPort());
            tcpSocket.connect(destinationAddress);
            log.info("TCP connection with {} successfully established.", tcpSocket.getInetAddress().toString());
        } catch (IOException e) {
            log.error("Failed to connect with {}.", destinationAddress);
        }
        return tcpSocket;
    }

    @Override
    public void send(Socket tcpSocket, byte[] message) {
        String address = tcpSocket.getInetAddress().toString();
        if (!isConnected(tcpSocket)) {
            log.error("Failed to send message to {} using TCP. There's no such connection!", address);
            return;
        }

        try {
            OutputStream os = tcpSocket.getOutputStream();
            os.write(message);
            log.info("Message successfully sent to {} using TCP.", address);
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
                log.info("TCP connection to {} successfully closed.", address);
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
