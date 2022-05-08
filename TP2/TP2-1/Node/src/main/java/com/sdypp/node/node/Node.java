package com.sdypp.node.node;

import com.sdypp.node.client.Peer2PeerClient;
import com.sdypp.node.server.Peer2PeerServer;
import com.sdypp.node.client.Client;
import com.sdypp.node.server.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Node {
    private Peer2PeerServer server;
    private Peer2PeerClient client;

    // thread both client and server
}
