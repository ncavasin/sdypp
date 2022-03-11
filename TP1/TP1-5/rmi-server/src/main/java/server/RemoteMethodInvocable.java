package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Due to the fact that this interface extends Remote and exposes methods, any implementer will
 * be ready to serve them through RMI
 */
public interface RemoteMethodInvocable extends Remote {

    // Echo back received message
    String echo(String msg) throws RemoteException;

    // Let the client know who the implementer is
    String identifyYourself() throws RemoteException;

    // Let the client know how the climate is at the implementer's location
    String getClimateConditions() throws RemoteException;

    // Let the client know who the implementer is AND how the climate is at the implementer's location (or so...)
    String getFullReport() throws RemoteException;

}
