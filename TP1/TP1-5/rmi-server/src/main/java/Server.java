import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmi.ClimateStatus;
import rmi.WeatherForecasterImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class Server {
    private static final String USAGE_MESSAGE = "WeatherForecasterImpl IP address and/or port are missing";
    /* just for fun */
    private static final HashMap<Integer, String> names = new HashMap<>();
    private static final HashMap<Integer, ClimateStatus> climates = new HashMap<>();
    private static int port;
    // Represents the current IP address of the system -> required for the registry
    private static String ipAddress;

    public static void main(String[] args) {
        checkUsage(args.length);
        setUp(args);

        log.info("Bootstrapping RMI server...");

        System.setProperty("java.rmi.server.hostname", ipAddress);

        // Create a new forecaster with randomized data
        WeatherForecasterImpl weatherForecaster = getWeatherForecaster();

        Registry registry = createRegistry(weatherForecaster);

        // Bind exported stub to received port to accept incoming consumptions from client through TCP sockets
        bind(weatherForecaster, registry);

        log.info("RMI server running at [{}:{}] ...", weatherForecaster.getIpAddress(), weatherForecaster.getPort());
    }

    private static void bind(WeatherForecasterImpl weatherForecaster, Registry registry) {
        try {
            // Bind the forecaster to a name in order to be found from remote clients by that name
            registry.rebind(weatherForecaster.getName(), weatherForecaster);
            log.info("Weather forecaster bounded successfully to port {}. Client lookup name is: {}.", weatherForecaster.getPort(), weatherForecaster.getName());
        } catch (RemoteException e) {
            panic(String.format("FATAL: failed to bind weather forecaster to name %s at port %d", weatherForecaster.getName(), weatherForecaster.getPort()), e);
        }
    }

    private static Registry createRegistry(WeatherForecasterImpl weatherForecaster) {
        // Create a new registry at received port
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(weatherForecaster.getPort());
            log.info("Registry created successfully at port {}.", weatherForecaster.getPort());
        } catch (RemoteException e) {
            panic(String.format("FATAL Could not create a new Registry at port %d", weatherForecaster.getPort()), e);
        }
        return registry;
    }

    private static WeatherForecasterImpl getWeatherForecaster() {
        WeatherForecasterImpl weatherForecaster = null;
        try {
            weatherForecaster = new WeatherForecasterImpl(pickNameAtRandom(), pickLocationAtRandom(), ipAddress, port);
            log.info("Weather forecaster {} initialized successfully!", weatherForecaster.getName());
        } catch (RemoteException e) {
            panic("Error while initializing weather forecaster", e);
        }
        return weatherForecaster;
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
}
