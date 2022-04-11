package rmi;

import dto.IdentificationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import shared.Task;
import shared.TaskProcessor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

@EqualsAndHashCode(callSuper = false)
@Data
public class TaskProcessorImpl extends UnicastRemoteObject implements TaskProcessor {

    private int port;
    private String ipAddress;
    private long pid;
    private String name;
    private static final Logger log = Logger.getLogger(TaskProcessorImpl.class.getName());

    public TaskProcessorImpl(String name, String ipAddress, int port) throws RemoteException {
        super();
        setPid(ProcessHandle.current().pid());
        setIpAddress(ipAddress);
        setPort(port);
        setName(name);
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
    public <T> T executeTask(Task<T> t) throws RemoteException {
        return null;
    }
}
