# RMI Overview
Per [Oracle](https://docs.oracle.com/javase/tutorial/rmi/overview.html) definition:

>RMI applications often comprise two separate programs, a server and a client. A typical server program creates some remote objects, makes references to these objects accessible, and waits for clients to invoke methods on these objects. A typical client program obtains a remote reference to one or more remote objects on a server and then invokes methods on them. RMI provides the mechanism by which the server and the client communicate and pass information back and forth. Such an application is sometimes referred to as a distributed object application.

## Architecture
![architecture](https://raw.githubusercontent.com/ncavasin/sdypp/main/TP1/TP1-5/rmi_arq.png)

## Remote object registration
As we can see in the diagram above both Client and Server interact with the ``Registry``: a fundamental piece in the architecture.

Usage is:
- Server creates a new ``Registry`` assigning a socket (IP:PORT) to it.
- Once created, Server will bind a Service layer as remote object to a unique name for the client to identify it.
- Client fetches ``Registry`` using the socket.
- Client uses``Registry`` to *lookup* remote objects by its name and invoke its methods.  

RMI uses Java object serialization mechanism to transport objects by value between different JVMs. So, every object must implement ``java.io.Serializable`` marker interface.

## Our solution
- Service layer implementing an interface that extends ``Remote`` interface and defines other methods as well.
- Server exposing & serving that interface.
- Client consuming interface's methods remotely as if they were invoked locally.

In other words, there's a shared interface between Server and Client where the former defines the exposed methods and the latter consumes them using RMI's architecture.

## Usage:

Containerize both jars in separate services and make them interact

## Metrics


## References:
https://docs.oracle.com/javase/tutorial/rmi

