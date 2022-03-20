import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmi.VectorOperatorImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class Server {
    private static final String USAGE_MESSAGE = "VectorOperatorImpl IP address and/or port are missing";
    /* just for fun */
    private static final HashMap<Integer, String> names = new HashMap<>();
    private static int port;
    // Represents the current IP address of the system -> required for the registry
    private static String ipAddress;

    public static void main(String[] args) {
        checkUsage(args.length);
        setUp(args);

        log.info("Bootstrapping RMI server...");

        System.setProperty("java.rmi.server.hostname", ipAddress);

        // Create a new forecaster with randomized data
        VectorOperatorImpl vectorMath = getVectorMath();

        Registry registry = createRegistry(vectorMath);

        // Bind exported stub to received port to accept incoming consumptions from client through TCP sockets
        bind(vectorMath, registry);

        log.info("RMI server running at [{}:{}] ...", vectorMath.getIpAddress(), vectorMath.getPort());
    }

    private static void bind(VectorOperatorImpl vectorMath, Registry registry) {
        try {
            // Bind the forecaster to a name in order to be found from remote clients by that name
            registry.rebind(vectorMath.getName(), vectorMath);
            log.info("Weather forecaster bounded successfully to port {}. Client lookup name is: {}.", vectorMath.getPort(), vectorMath.getName());
        } catch (RemoteException e) {
            panic(String.format("FATAL: failed to bind weather forecaster to name %s at port %d", vectorMath.getName(), vectorMath.getPort()), e);
        }
    }

    private static Registry createRegistry(VectorOperatorImpl vectorMath) {
        // Create a new registry at received port
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(vectorMath.getPort());
            log.info("Registry created successfully at port {}.", vectorMath.getPort());
        } catch (RemoteException e) {
            panic(String.format("FATAL Could not create a new Registry at port %d", vectorMath.getPort()), e);
        }
        return registry;
    }

    private static VectorOperatorImpl getVectorMath() {
        VectorOperatorImpl vectorMath = null;
        try {
            vectorMath = new VectorOperatorImpl(pickNameAtRandom(), ipAddress, port);
            log.info("Weather forecaster {} initialized successfully!", vectorMath.getName());
        } catch (RemoteException e) {
            panic("Error while initializing weather forecaster", e);
        }
        return vectorMath;
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
