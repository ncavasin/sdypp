import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rmi.TaskProcessorImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Random;


@Slf4j
@RequiredArgsConstructor
public class Server {
    private static final String USAGE_MESSAGE = "TaskProcessorImpl IP address and/or port are missing";
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
        TaskProcessorImpl taskProcessor = getTaskProcessor();

        Registry registry = createRegistry(taskProcessor);

        // Bind exported stub to received port to accept incoming consumptions from client through TCP sockets
        bind(taskProcessor, registry);

        log.info("RMI server running at [{}:{}] ...", taskProcessor.getIpAddress(), taskProcessor.getPort());
    }

    private static void bind(TaskProcessorImpl taskProcessor, Registry registry) {
        try {
            // Bind the task processor to a name in order to be found from remote clients by that name
            registry.rebind(taskProcessor.getName(), taskProcessor);
            log.info("Task processor bounded successfully to port {}. Client lookup name is: {}.", taskProcessor.getPort(), taskProcessor.getName());
        } catch (RemoteException e) {
            panic(String.format("FATAL: failed to bind Task processor to name %s at port %d", taskProcessor.getName(), taskProcessor.getPort()), e);
        }
    }

    private static Registry createRegistry(TaskProcessorImpl taskProcessor) {
        // Create a new registry at received port
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(taskProcessor.getPort());
            log.info("Registry created successfully at port {}.", taskProcessor.getPort());
        } catch (RemoteException e) {
            panic(String.format("FATAL Could not create a new Registry at port %d", taskProcessor.getPort()), e);
        }
        return registry;
    }

    private static TaskProcessorImpl getTaskProcessor() {
        TaskProcessorImpl taskProcessor = null;
        try {
            taskProcessor = new TaskProcessorImpl(pickNameAtRandom(), ipAddress, port);
            log.info("Task processor {} initialized successfully!", taskProcessor.getName());
        } catch (RemoteException e) {
            panic("Error while initializing Task processor", e);
        }
        return taskProcessor;
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
