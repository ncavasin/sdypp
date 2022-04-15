# RMI Overview

Per [Oracle](https://docs.oracle.com/javase/tutorial/rmi/overview.html) definition:

> RMI applications often comprise two separate programs, a server and a client. A typical server program creates some
> remote objects, makes references to these objects accessible, and waits for clients to invoke methods on these
> objects.
> A typical client program obtains a remote reference to one or more remote objects on a server and then invokes methods
> on them. RMI provides the mechanism by which the server and the client communicate and pass information back and
> forth.
> Such an application is sometimes referred to as a distributed object application.

## Description

This exercise presents a Server who implements the ``Remote`` interface and a Client that invokes its methods remotely
through RMI.
In other words, there's a shared interface between Server and Client where the former defines the exposed method/s and
the latter consume/s them using RMI's architecture.

**Note:** When a remote method is invoked, the execution -for the **developer**- will be completely transparent. Just
like any other regular method execution.

## Architecture

![architecture](https://raw.githubusercontent.com/ncavasin/sdypp/main/TP1/TP1-5/rmi_arq.png)

## Remote object registration

As we can see in the diagram above both Client and Server interact with the ``Registry``: a fundamental piece in the
structure.

Usage is:

- Server creates a new ``Registry`` assigning a socket (IP:PORT) to it.
- Once created, Server will bind a remote object (implementing the shared interface w/ client) to a unique name for the
  client to identify it.
- Client fetches ``Registry`` using the socket.
- Client uses``Registry`` to *lookup* remote objects by its name and invoke its methods.

So, how does an object becomes RMI*able*? Simple, implementing Java's built-in ``Remote`` interface.

# Usage:

Use one terminal for executing first the Server and then another one for executing the Client.

Server:

```bash
wget https://github.com/ncavasin/sdypp/raw/main/TP1/TP1-6/rmi-server-e6.jar
java -jar rmi-server-e5.jar <YOUR_IP_ADDRESS> <EPHEMERAL_PORT>
```

**Note**: Watch the logs and remember the name of the Server as it's needed as a param for the Client.

Client:

```bash
wget https://github.com/ncavasin/sdypp/raw/main/TP1/TP1-6/rmi-client-e6.jar
java -jar rmi-client-e5.jar <YOUR_IP_ADDRESS> <EPHIMEREAL_PORT> <SERVER_NAME>
```

**Note**: Watch the logs from Server to get the name and use it as the param for Client. Otherwise RMI registry lookup won't work.
