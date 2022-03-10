import lombok.RequiredArgsConstructor;
import server.Server;
import utils.ClimateStatus;

import java.util.HashMap;
import java.util.Random;

@RequiredArgsConstructor
public class Main {

    /* just for fun */
    private static final HashMap<Integer, String> names = new HashMap<>();
    private static final HashMap<Integer, ClimateStatus> climates = new HashMap<>();
    private final Server server;

    public static void main(String[] args) {
        setUp();
        Server server = new Server(pickNameAtRandom(), pickLocationAtRandom());
    }

    private static void setUp() {
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
