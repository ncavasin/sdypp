package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Due to the fact that this interface extends Remote and exposes methods, any implementer will
 * be ready to serve them through RMI
 */
public interface VectorOperator extends Remote {

    // Echo back received message
    String echo(String msg) throws RemoteException;

    // Let the client know who the implementer is
    String identifyYourself() throws RemoteException;

    VectorOperationResultDto addition(List<Float> v1, List<Float> v2) throws RemoteException;

    VectorOperationResultDto subtraction(List<Float> v1, List<Float> v2) throws RemoteException;

}