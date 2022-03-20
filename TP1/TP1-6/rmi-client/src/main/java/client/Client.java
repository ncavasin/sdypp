package client;

import lombok.extern.slf4j.Slf4j;
import shared.VectorOperationResultDto;
import shared.VectorOperator;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

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
        VectorOperator vectorMath = getVectorMath(registry);

        try {
            String msg = "I am invoking you through RMI";
            log.info("Client requested echo of <{}>...", msg);
            log.info("SERVER => {}", vectorMath.echo(msg));
        } catch (RemoteException e) {
            log.warn("Remote method invocation failed: {}", e.getMessage());
            e.printStackTrace();
        }

        try {
            log.info("Client asked Server to identify itself...");
            log.info("SERVER => {}", vectorMath.identifyYourself());
        } catch (RemoteException e) {
            log.warn("Remote method invocation failed: {}", e.getMessage());
            e.printStackTrace();
        }

        // Define two vectors
        List<Float> v1 = List.of(9.8F, 5F, 2.7F, 1F, 1F);
        log.info("Vector 1 = {}", v1);
        List<Float> v2 = List.of(0.2F, 5F, 2.3F, 1F, 9F);
        log.info("Vector 2 = {}", v2);

        try {
            VectorOperationResultDto vectorOperationResultDto = vectorMath.addition(v1, v2);
            log.info("Addition = {} ", vectorOperationResultDto.toString());
        } catch (RemoteException e) {
            log.warn("Remote method invocation failed: {}", e.getMessage());
            e.printStackTrace();
        }

        try {
            VectorOperationResultDto vectorOperationResultDto = vectorMath.subtraction(v1, v2);
            log.info("Addition = {} ", vectorOperationResultDto.toString());
        } catch (RemoteException e) {
            log.warn("Remote method invocation failed: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static VectorOperator getVectorMath(Registry registry) {
        VectorOperator vectorMath = null;
        try {
            vectorMath = (VectorOperator) registry.lookup(rmiObjectName);
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
