package com.sdypp.code.rmi;

import com.sdypp.code.dto.IdentificationDto;
import com.sdypp.code.shared.WeatherForecaster;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringJoiner;

@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
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
        log.info(String.format("Echo() invoked. Received message is: <%s>.", msg));
        return "...echoing back message <" + msg + ">. Goodbye!";
    }

    @Override
    public String identifyYourself() {
        log.info("IdentifyYourself() invoked.");
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
        log.info("GetClimateConditions() invoked.");
        return getClimateStatus().toString();
    }

    @Override
    public String getFullReport() {
        log.info("GetFullReport() invoked.");
        StringJoiner stringJoiner = new StringJoiner("\n");
        return stringJoiner
                .add(identifyYourself())
                .add(getClimateConditions())
                .toString();
    }
}
