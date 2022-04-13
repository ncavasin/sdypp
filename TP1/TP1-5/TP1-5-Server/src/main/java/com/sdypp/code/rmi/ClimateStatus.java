package com.sdypp.code.rmi;

import lombok.Builder;
import lombok.Data;

import java.util.StringJoiner;

@Data
@Builder
public class ClimateStatus {
    private String country;
    private String temperature;
    private String humidity;

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        return stringJoiner.add("Current climate conditions in " + country + " are:")
                .add("Temperature: " + temperature + ".")
                .add("Humidity: " + humidity + ".")
                .toString();
    }
}
