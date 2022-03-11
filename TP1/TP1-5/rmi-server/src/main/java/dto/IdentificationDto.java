package dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

@Data
@Builder
public class IdentificationDto {

    /* Sender's IP address */
    private String ipAddress;

    /* Sender's port */
    private int port;

    /* Sender's process ID */
    private long pid;

    /* Sender's name */
    private String name;

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        return stringJoiner
                .add("Hello! My name is " + pid)
                .add("But my friends call me " + name)
                .add("You can find me at " + ipAddress + ":" + port)
                .toString();
    }
}
