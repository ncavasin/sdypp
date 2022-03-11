package server;

import dto.IdentificationDto;
import lombok.Data;
import dto.ClimateStatus;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.StringJoiner;

@Data
public class Server extends UnicastRemoteObject implements RemoteMethodInvocable{

    private int port;
    private String ipAddress;
    private long pid;
    private String name;
    private ClimateStatus climateStatus;

    public Server(String name, ClimateStatus climateStatuses, String ipAddress, int port) throws RemoteException {
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
