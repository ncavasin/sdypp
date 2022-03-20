package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Due to the fact that this interface extends Remote and exposes methods, any implementer will
 * be ready to serve them through RMI
 */
public interface VectorMath extends Remote {

    // Echo back received message
    String echo(String msg) throws RemoteException;

    // Let the client know who the implementer is
    String identifyYourself() throws RemoteException;

    Float[] add(Float[] v1, Float[] v2);

    Float[] substract(Float[] v1, Float[] v2);

}