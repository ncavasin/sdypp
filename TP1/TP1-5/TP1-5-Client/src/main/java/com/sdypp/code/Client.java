package com.sdypp.code;

import com.sdypp.code.shared.WeatherForecaster;
import lombok.extern.slf4j.Slf4j;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
        Registry registry;
        WeatherForecaster weatherForecaster;
        try {

            // Locate the RMI registry using received socket
            registry = getRegistry();

            try {// Lookup the exposed weather forecaster object in the registry
                weatherForecaster = getWeatherForecaster(registry);
                try {
                    String msg = "I am invoking you through RMI";
                    log.info("Client requested echo of <{}>...", msg);
                    log.info("SERVER => {}", weatherForecaster.echo(msg));
                } catch (RemoteException e) {
                    log.warn("Remote method invocation failed: {}", e.getMessage());
                    e.printStackTrace();
                }

                try {
                    log.info("Client asked Server to identify itself...");
                    log.info("SERVER => {}", weatherForecaster.identifyYourself());
                } catch (RemoteException e) {
                    log.warn("Remote method invocation failed: {}", e.getMessage());
                    e.printStackTrace();
                }

                // Get climate conditions where the server is located at
                try {
                    log.info("Client asked Server for a climate conditions...");
                    log.info("SERVER => {}", weatherForecaster.getClimateConditions());
                } catch (RemoteException e) {
                    log.warn("Remote method invocation failed: {}", e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                log.error("Lookup of server name inside registry failed. Status: {}.", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Could not locate registry. Status: {}.", e.getMessage());
        }
    }

    private static WeatherForecaster getWeatherForecaster(Registry registry) {
        WeatherForecaster weatherForecaster = null;
        try {
            weatherForecaster = (WeatherForecaster) registry.lookup(rmiObjectName);
            log.info("Lookup success: remote object {} found at {}:{}", rmiObjectName, ipAddress, port);
        } catch (RemoteException e) {
            log.warn("Lookup failure: remote object '{}' NOT FOUND.", rmiObjectName);
        } catch (NotBoundException e) {
            log.error("Registry not bound. Error:{}", e.getMessage());
        }
        return weatherForecaster;
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

    private static void panic(String usageMessage, Exception e) {

    }

}
