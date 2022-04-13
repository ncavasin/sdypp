package com.example.demo.rmi;

import com.example.demo.dto.IdentificationDto;
import com.example.demo.shared.VectorOperationResultDto;
import com.example.demo.shared.VectorOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class VectorOperatorImpl extends UnicastRemoteObject implements VectorOperator {

    private int port;
    private String ipAddress;
    private long pid;
    private String name;

    public VectorOperatorImpl(String name, String ipAddress, int port) throws RemoteException {
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
    public VectorOperationResultDto addition(List<Float> v1, List<Float> v2) {
        log.info("addition() invoked.");
        List<Float> result = new ArrayList<>();

        if (lengthsDiffer(v1, v2))
            return VectorOperationResultDto.builder().build();

        v1 = shouldFlipRandom(v1.size()) ? flip(v1) : v1;

        for (int i = 0; i < v1.size(); i++) {
            result.add(v1.get(i) + v2.get(i));
        }

        return VectorOperationResultDto.builder()
                .v1(v1)
                .v2(v2)
                .result(result)
                .build();

    }

    @Override
    public VectorOperationResultDto subtraction(List<Float> v1, List<Float> v2) {
        log.info("subtraction() invoked.");
        List<Float> result = new ArrayList<>();

        if (lengthsDiffer(v1, v2))
            return VectorOperationResultDto.builder().build();

        v1 = shouldFlipRandom(v1.size()) ? flip(v1) : v1;

        for (int i = 0; i < v1.size(); i++) {
            result.add(v1.get(i) - v2.get(i));
        }

        return VectorOperationResultDto.builder()
                .v1(v1)
                .v2(v2)
                .result(result)
                .build();
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
