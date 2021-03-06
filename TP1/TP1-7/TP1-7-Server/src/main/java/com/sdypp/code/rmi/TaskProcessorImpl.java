package com.sdypp.code.rmi;

import com.sdypp.code.dto.IdentificationDto;
import com.sdypp.code.shared.Task;
import com.sdypp.code.shared.TaskProcessor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
        return t.execute();
    }
}
