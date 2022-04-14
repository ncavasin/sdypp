package com.sdypp.code;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Due to the fact that this interface extends Remote and exposes methods, any implementer will
 * be ready to serve them through RMI
 */
public interface TaskProcessor extends Remote {

    // Echo back received message
    String echo(String msg) throws RemoteException;

    // Let the client know who the implementer is
    String identifyYourself() throws RemoteException;

    <T> T executeTask(Task <T> t)  throws RemoteException;

}