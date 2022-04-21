package com.sdypp.master;

import com.sdypp.master.connection.ConnectionHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

@Data
@Slf4j
public class MasterApplication {
    private Hashtable<InetSocketAddress, List<String>> filesBySocket;

    public MasterApplication(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("Master listening on port {}...", port);

            // fetch file names from other masters

            // Get ready to serve them
            try {
                BufferedReader br = null;
                PrintWriter pw = null;

                while (true) {
                    Socket client = serverSocket.accept();
                    br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    pw = new PrintWriter(client.getOutputStream(), true);
                    ConnectionHandler connectionHandler = new ConnectionHandler(client);
                }
            } catch (Exception e) {
                log.error("Master failed when accepting connection. {}", e.getMessage());
                System.exit(-1);
            }

        } catch (IOException e) {
            log.info("Master failed to start on port {}. IOException: {}.", port, e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        new MasterApplication(6600);

    }
}
