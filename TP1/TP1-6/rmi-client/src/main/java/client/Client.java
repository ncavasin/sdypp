package client;

import lombok.extern.slf4j.Slf4j;
import shared.VectorMath;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

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

        // Lookup the exposed weather forecaster object in the registry
        VectorMath vectorMath = getVectorMath(registry);

        // Add two vectores
        Float[] v1 = {9.8F, 5F, 0.7F, 2F};
        Float[] v2 = {0.2F, 5F, 0.3F, 3F};
        try {
            Float[] result = vectorMath.add(v1, v2);
            System.out.println("Result is =" + Arrays.toString(result));
        } catch (RemoteException e) {
            log.warn("Remote method invocation failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static VectorMath getVectorMath(Registry registry) {
        VectorMath vectorMath = null;
        try {
            vectorMath = (VectorMath) registry.lookup(rmiObjectName);
            log.info("Lookup success: remote object {} found at {}:{}", rmiObjectName, ipAddress, port);
        } catch (RemoteException e) {
            log.warn("Lookup failure: remote object '{}' NOT FOUND", rmiObjectName);
            log.warn(e.getMessage());
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return vectorMath;
    }

    private static Registry getRegistry() {
        // Locate the registry create by the server at received socket
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(ipAddress, port);
            log.info("Registry located at socket {}:{}.", ipAddress, port);
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
        log.error(msg);
        if (e != null) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        System.exit(1);
    }

}
