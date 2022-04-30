package com.sdypp.node.node;

import com.sdypp.node.shared.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractServer implements Server {
    private ServerSocket serverSocket;

    private Socket currentClient;

    private BufferedReader bufferedReader;

    private BufferedWriter bufferedWriter;
}
