package com.sdypp.code;


import com.sdypp.code.shared.TaskProcessor;
import lombok.extern.slf4j.Slf4j;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;


@Slf4j
public class Client {
    private static final String USAGE_MESSAGE = "Missing at least one required argument: <IP_address> <port> <rmi_server_name>";
    private static String ipAddress;
    private static int port;
    private static String rmiObjectName;


    public static void main(String[] args) {
        checkUsage(args.length);
        setUp(args);
        log.info("Bootstrapping RMI client...");
        // Locate the RMI registry using received socket
        Registry registry = getRegistry();

        // Lookup the exposed task processor object in the registry
        TaskProcessor taskProcessor = getTaskProcessor(registry);

        try {
            String msg = "I am invoking you through RMI";
            log.info(String.format("Client requested echo of <%s>...", msg));
            log.info(String.format("SERVER => %s", taskProcessor.echo(msg)));
        } catch (RemoteException e) {
            log.info(String.format("Remote method invocation failed: %s", e.getMessage()));
            e.printStackTrace();
        }

        try {
            log.info("Client asked Server to identify itself...");
            log.info(String.format("SERVER => %s", taskProcessor.identifyYourself()));
        } catch (RemoteException e) {
            log.info(String.format("Remote method invocation failed: %s", e.getMessage()));
            e.printStackTrace();
        }
    }

    private static TaskProcessor getTaskProcessor(Registry registry) {
        TaskProcessor taskProcessor = null;
        try {
            taskProcessor = (TaskProcessor) registry.lookup(rmiObjectName);
            log.info(String.format("Lookup success: remote object %s found at %s:%d", rmiObjectName, ipAddress, port));
        } catch (RemoteException e) {
            log.info(String.format("Lookup failure: remote object '%s' NOT FOUND", rmiObjectName));
            log.info(e.getMessage());
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return taskProcessor;
    }

    private static Registry getRegistry() {
        // Locate the registry create by the server at received socket
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(ipAddress, port);
            log.info(String.format("Registry located at socket %s:%d.", ipAddress, port));
        } catch (RemoteException e) {
            panic(String.format("FATAL Could not find Registry at socket %s:%d", ipAddress, port), e);
        }
        return registry;
    }

    private static void setUp(String[] args) {
        ipAddress = args[0];
        port = Integer.parseInt(args[1]);
        if (port > 65536 | port < 1023) panic("Port invalid range!", null);
        rmiObjectName = args[2];
    }

    private static void checkUsage(int size) {
        if (size != 3) panic(USAGE_MESSAGE, null);
    }

    private static void panic(String msg, Exception e) {
        log.info(msg);
        if (e != null) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        System.exit(1);
    }
}
