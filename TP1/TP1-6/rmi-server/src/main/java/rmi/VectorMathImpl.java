package rmi;

import dto.IdentificationDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import shared.VectorMath;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@EqualsAndHashCode(callSuper = false)
@Data
public class VectorMathImpl extends UnicastRemoteObject implements VectorMath {

    private int port;
    private String ipAddress;
    private long pid;
    private String name;
    private ClimateStatus climateStatus;

    public VectorMathImpl(String name, String ipAddress, int port) throws RemoteException {
        super();
        setPid(ProcessHandle.current().pid());
        setIpAddress(ipAddress);
        setPort(port);
        setName(name);
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
    public Float[] add(Float[] v1, Float[] v2) {
        if (lengthsDiffer(v1, v2))
            return new Float[0];

        Float[] result = new Float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    @Override
    public Float[] substract(Float[] v1, Float[] v2) {
        if (lengthsDiffer(v1, v2))
            return new Float[0];

        Float[] result = new Float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    private boolean lengthsDiffer(Float[] v1, Float[] v2) {
        return v1.length != v2.length;
    }
}
