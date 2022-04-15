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

## Remote task execution

TaskProcessor's implementation never needs neither GenerateRandomInteger nor GenerateRandomString classes' definition until an object of that kind is passed in as an argument to the executeTask method. At that point, the code for the class is loaded by RMI into the TaskProcessor's object's JVM, the execute method is invoked, and the task's code is executed. Finally, the result is handed back to the calling client, where it is used to print the result of the computation.

The fact that the supplied Task object computes the generation of a random integer or string is irrelevant to the TaskProcessor object. You could also implement a task that, for example, calculates the value of Pi number. The whole point of this is that the Client can now offload the computational expensive calculations to the Server without the server having to know explicitly how to do it until RMI fetches the needed classes.

# Usage:

Use one terminal for executing first the Server and then another one for executing the Client.

Server:

```bash
wget https://github.com/ncavasin/sdypp/raw/main/TP1/TP1-7/rmi-server-e7.jar
java -jar rmi-server-e7.jar <YOUR_IP_ADDRESS> <EPHEMERAL_PORT>
```

**Note**: Watch the logs and remember the name of the Server as it's needed as a param for the Client.

Client:

```bash
wget https://github.com/ncavasin/sdypp/raw/main/TP1/TP1-7/rmi-client-e7.jar
java -jar rmi-client-e7.jar <YOUR_IP_ADDRESS> <EPHIMEREAL_PORT> <SERVER_NAME>
```

**Note**: Watch the logs from Server to get the name and use it as the param for Client. Otherwise RMI registry lookup won't work.
