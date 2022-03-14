package dto;

import lombok.Builder;
import lombok.Data;

import java.util.StringJoiner;

@Data
@Builder
public class IdentificationDto {

    // socket
    private String ipAddress;
    private int port;

    // process ID
    private long pid;

    // server's name
    private String name;

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\n\t\t\t");
        return stringJoiner
                .add("Hello! My name is PID " + pid)
                .add("But my friends call me " + name + " (RMI lookup value)")
                .add("You can find me at " + ipAddress + ":" + port + " (RMI TCP socket)")
                .toString();
    }
}
