# RMI Overview
Per [Oracle](https://docs.oracle.com/javase/tutorial/rmi/overview.html) definition:

>RMI applications often comprise two separate programs, a server and a client. A typical server program creates some remote objects, makes references to these objects accessible, and waits for clients to invoke methods on these objects. A typical client program obtains a remote reference to one or more remote objects on a server and then invokes methods on them. RMI provides the mechanism by which the server and the client communicate and pass information back and forth. Such an application is sometimes referred to as a distributed object application.

# Description
This exercise presents a Server who implements the ``Remote`` interface. That means that it's exposing the methods defined in the ``RemoteMethodInvocable`` interface for consumption, remaining idle until a Client -located at another JVM or not- decides to invoke one of them.

When invoked, the execution -for the **developer**- will be completely transparent. Just like any other regular method execution.

# Architecture
![architecture](https://raw.githubusercontent.com/ncavasin/sdypp/main/TP1/TP1-5/rmi_arq.png)

# Remote object registration
As we can see in the diagram above both Client and Server interact with the ``Registry``: a fundamental piece in the structure.

Usages are:
- Server calls ``Registry`` to bind a name with a remote object (That's how it exposes the methods inside the object it's ready to serve)
- Client queries ``Registry`` to find a remote object by its name. (That's how a client invokes a method inside an object).  

Every method defined in ``RemoteMethodInvocable`` interface **must** be referenced in ``Registry`` by the Server. Otherwise, Server will be exposing an invalid interface that might lead to undefined behaviour and Client won't be able to execute whatever it was supposed to execute.

So, how does an object becomes RMI*able*? Simple, implementing ``Remote`` interface.

