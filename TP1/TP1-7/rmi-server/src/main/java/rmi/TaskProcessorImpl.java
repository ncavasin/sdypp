package rmi;

import dto.IdentificationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import shared.Task;
import shared.TaskProcessor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class TaskProcessorImpl extends UnicastRemoteObject implements TaskProcessor {

    private int port;
    private String ipAddress;
    private long pid;
    private String name;

    public TaskProcessorImpl(String name, String ipAddress, int port) throws RemoteException {
        super();
        setPid(ProcessHandle.current().pid());
        setIpAddress(ipAddress);
        setPort(port);
        setName(name);
    }

    @Override
    public String echo(String msg) {
        log.info("Echo() invoked. Received message is: <{}>.", msg);
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
    public <T> T executeTask(Task<T> t) throws RemoteException {
        return null;
    }

    private List<Float> flip(List<Float> v1) {
        return v1.stream().map(e -> e * (-1)).collect(Collectors.toList());
    }

    private boolean shouldFlipRandom(int upperLimit) {
        return (new Random().ints(1, upperLimit)
                .findFirst()
                .getAsInt() % 2) == 0;
    }

    private boolean lengthsDiffer(List<Float> v1, List<Float> v2) {
        return v1.size() != v2.size();
    }
}
