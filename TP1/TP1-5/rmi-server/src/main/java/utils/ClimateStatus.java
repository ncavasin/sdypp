package utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClimateStatus {
    private String country;
    private String temperature;
    private String humidity;
}
