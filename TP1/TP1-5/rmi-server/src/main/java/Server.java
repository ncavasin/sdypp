import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmi.WeatherForecasterImpl;
import rmi.ClimateStatus;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class Server {
    private static final String USAGE_MESSAGE = "WeatherForecasterImpl IP address and/or port are missing";

    /* just for fun */
    private static final HashMap<Integer, String> names = new HashMap<>();
    private static final HashMap<Integer, ClimateStatus> climates = new HashMap<>();
    private static String ipAddress;
    private static int port;

    public static void main(String[] args) {
        checkUsage(args.length);
        setUp(args);

        log.info("Bootstrapping RMI server...");

        WeatherForecasterImpl weatherForecaster = getWeatherForecaster();

        WeatherForecasterImpl weatherForecasterStub = getStub(weatherForecaster);

        log.info("RMI server running.....");
    }

    private static WeatherForecasterImpl getStub(WeatherForecasterImpl remoteMethodInvocableImpl) {
        WeatherForecasterImpl stub = null;
        try {
            // Export the stub to receive remote invocations over TCP connections at received port or default 6060
            stub = (WeatherForecasterImpl) UnicastRemoteObject.exportObject(remoteMethodInvocableImpl, remoteMethodInvocableImpl != null ? remoteMethodInvocableImpl.getPort() : 6060);

            log.info("RMI stub instantiated successfully.");

            // Get a reference to the Remote interface registry. Well-known port is 1099
            Registry registry = LocateRegistry.getRegistry();

            // Bind the stub name (aka "WeatherForecasterImpl" class) to the received port to be found from remote clients by its name
            registry.rebind("WeatherForecasterImpl", stub);

            log.info("RMI stub bounded successfully to port {}.", stub.getPort());
        } catch (RemoteException e) {
            log.warn(e.getMessage());
        }
        return stub;
    }

    private static WeatherForecasterImpl getWeatherForecaster() {
        WeatherForecasterImpl weatherForecaster = null;
        try {
            weatherForecaster = new WeatherForecasterImpl(pickNameAtRandom(), pickLocationAtRandom(), ipAddress, port);
            log.info("RMI remoteMethodInvocableImpl instantiated successfully!");
        } catch (RemoteException e) {
            panic("Error while initializing RMI remoteMethodInvocableImpl", e);
        }
        return weatherForecaster;
    }

    private static void checkUsage(int size) {
        if (size != 2) panic(USAGE_MESSAGE, null);
    }

    private static void panic(String msg, Exception e) {
        log.error(msg);
        if (e != null) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        System.exit(1);
    }

    private static void setUp(String[] args) {
        ipAddress = args[0];
        port = Integer.parseInt(args[1]);
        if (port > 65536 | port < 1023) panic("Port invalid range!", null);

        names.put(1, "JARVIS");
        names.put(2, "R2-D2");
        names.put(3, "C3PIO");
        names.put(4, "BENDER");
        names.put(5, "TERMINATOR");

        climates.put(1, ClimateStatus.builder()
                .country("BUENOS_AIRES")
                .temperature("19°c")
                .humidity("81%")
                .build());
        climates.put(2, ClimateStatus.builder()
                .country("NEW_YORK")
                .temperature("2°c")
                .humidity("91%")
                .build());
        climates.put(3, ClimateStatus.builder()
                .country("SYDNEY")
                .temperature("22°c")
                .humidity("49%")
                .build());
        climates.put(4, ClimateStatus.builder()
                .country("TEL_AVIV")
                .temperature("13°c")
                .humidity("84%")
                .build());
        climates.put(5, ClimateStatus.builder()
                .country("LONDON")
                .temperature("10°c")
                .humidity("72%")
                .build());
    }

    private static ClimateStatus pickLocationAtRandom() {
        Random random = new Random();
        int mappedNameKey = random.ints(1, names.size())
                .findFirst()
                .getAsInt();
        return climates.get(mappedNameKey);
    }

    private static String pickNameAtRandom() {
        Random random = new Random();
        int mappedNameKey = random.ints(1, names.size())
                .findFirst()
                .getAsInt();
        return names.get(mappedNameKey);
    }
}
