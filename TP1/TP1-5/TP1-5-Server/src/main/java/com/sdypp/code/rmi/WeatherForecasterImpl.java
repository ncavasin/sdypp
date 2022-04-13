package com.sdypp.code.rmi;

import com.sdypp.code.dto.IdentificationDto;
import com.sdypp.code.shared.WeatherForecaster;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringJoiner;

@EqualsAndHashCode(callSuper = false)
@Data
public class WeatherForecasterImpl extends UnicastRemoteObject implements WeatherForecaster {

    private int port;
    private String ipAddress;
    private long pid;
    private String name;
    private ClimateStatus climateStatus;

    public WeatherForecasterImpl(String name, ClimateStatus climateStatuses, String ipAddress, int port) throws RemoteException {
        super();
        setPid(ProcessHandle.current().pid());
        setIpAddress(ipAddress);
        setPort(port);
        setName(name);
        setClimateStatus(climateStatuses);
    }

    @Override
    public String echo(String msg) {
        return "...echoing back message <" + msg + ">. Goodbye!";
    }

    @Override
    public String identifyYourself() {
        return IdentificationDto.builder()
                .ipAddress(getIpAddress())
                .port(getPort())
                .pid(getPid())
                .name(getName())
                .build()
                .toString();
    }

    @Override
    public String getClimateConditions() {
        return getClimateStatus().toString();
    }

    @Override
    public String getFullReport() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        return stringJoiner
                .add(identifyYourself())
                .add(getClimateConditions())
                .toString();
    }
}
