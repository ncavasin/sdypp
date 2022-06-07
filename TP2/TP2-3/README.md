
# TP2 - Ejercicio 3

El objetivo de este ejercicio es crear y aplicar una red flexible de nodos la cual se adapte dependiendo su carga de trabajo. Para ello, se desarrolló una sencilla API el cual expone un expoint **POST** `/doSomething` que crea un array de un tamaño significante para consumir memoria y lo borra luego de 10 segundos.

El objetivo del siguiente informe es explicar y observar como se comporta esta API cuando obtiene mucho tráfico. La implementación de kubernetes permite que la APP se adapte a la carga de trabajo creando y eliminado replicas cuando sea necesario.

Al aplicar todos los manifiestos con el comando `$ kubectl apply -f .` y esperar unos segundos, obtendremos la siguiente informacion luego de ejecutar el siguiente comando:

> $ kubectl get all
```
NAME                         READY   STATUS    RESTARTS   AGE
pod/tp2-3-8655bfd59d-hszjj   1/1     Running   0          62m

NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/kubernetes   ClusterIP      10.124.0.1      <none>        443/TCP          5d22h
service/tp2-3        LoadBalancer   10.124.13.120   34.75.58.1    4001:31781/TCP   62m

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/tp2-3   1/1     1            1           62m

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/tp2-3-8655bfd59d   1         1         1       62m

NAME                                        REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/tp2-3   Deployment/tp2-3   21%/50%   1         4         1          62m
```

Con esta información podemos deducir que actualmente tenemos:
- Un deployment con actualmente un solo pod.
- Un Service de tipo LoadBalancer que expone al deployment y
- Un HPA que irá agregando o quitando nuevos pods en base al target. Este target es el 50% del uso promedio de la memoria. Por lo que si es superado se creará nuevos pods, y los eliminará si el uso cae por debajo de este target. Actualmente el uso se encuentra en 21% y eso es porque el pod se encuentra corriendo un Servidor el cual requiere memoria.

Si llamamos al endpoint que expone la app **POST** `/doSomething`, el uso de la memoria comenzará a incrementarse, pero para que superé el target del 50% hay que hacer muchos llamados consecutivos. Una forma de hacer esto es con el siguiente comando:
> curl -X POST -s http://34.75.58.1:4001/doSomething?[1-1000] -H "Connection: close"

*El header "Connection: close" hara que la conexion no sea keep-alive. Necesario para el buen funcionamiento del load balancer*

Este comando ejecuta 1000 veces la llamada al servidor, por lo que el uso de la memoria se incrementará sin dudas.
Si ejecutamos el comando en una nueva terminal y esperamos unos segundos, podremos empezar a ver como el servidor empieza a consumir mas memoria, superando el target del HPA:

> kubectl get hpa
```
NAME    REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
tp2-3   Deployment/tp2-3   83%/50%   1         4         4          65m
```

Como podemos ver, el consumo de memoria llegó a un 83%, superando el target. Esto hace que kubernetes aumente la cantidad de replicas a 4.

Si esperamos unos segundos a que los nuevos pods comiencen a funcionar, podemos observar como el uso promedio de memoria se redujo a 61%, como se muestra a continuacion:

> kubectl get all
```
NAME                         READY   STATUS    RESTARTS   AGE
pod/tp2-3-8655bfd59d-4sr2l   1/1     Running   0          3m41s
pod/tp2-3-8655bfd59d-gj7zw   1/1     Running   0          41s
pod/tp2-3-8655bfd59d-hszjj   1/1     Running   0          68m
pod/tp2-3-8655bfd59d-xsd76   1/1     Running   0          71s

NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/kubernetes   ClusterIP      10.124.0.1      <none>        443/TCP          5d23h
service/tp2-3        LoadBalancer   10.124.13.120   34.75.58.1    4001:31781/TCP   68m

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/tp2-3   4/4     4            4           68m

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/tp2-3-8655bfd59d   4         4         4       68m

NAME                                        REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/tp2-3   Deployment/tp2-3   61%/50%   1         4         4          68m
```

A continuacion se puede ver como la carga se distribuye en todos los pods:

> kubectl top pods
```
NAME                     CPU(cores)   MEMORY(bytes)
tp2-3-8655bfd59d-4sr2l   74m          21Mi
tp2-3-8655bfd59d-gj7zw   85m          23Mi
tp2-3-8655bfd59d-hszjj   79m          73Mi
tp2-3-8655bfd59d-xsd76   78m          64Mi
```

Si esperamos unos segundos mas, podemos ver como la carga promedio de la memoria bajó aun mas a partir de la distribucion del trafico entre los distintos pods:
> kubectl get hpa
```
NAME    REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
tp2-3   Deployment/tp2-3   45%/50%   1         4         4          69m
```


Ahora, si dejamos de hacer peticiones, podemos observar como el uso promedio de la memoria se reduce:
```
NAME                         READY   STATUS    RESTARTS   AGE
pod/tp2-3-8655bfd59d-4sr2l   1/1     Running   0          6m29s
pod/tp2-3-8655bfd59d-gj7zw   1/1     Running   0          3m29s
pod/tp2-3-8655bfd59d-hszjj   1/1     Running   0          71m
pod/tp2-3-8655bfd59d-xsd76   1/1     Running   0          3m59s

NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/kubernetes   ClusterIP      10.124.0.1      <none>        443/TCP          5d23h
service/tp2-3        LoadBalancer   10.124.13.120   34.75.58.1    4001:31781/TCP   71m

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/tp2-3   4/4     4            4           71m

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/tp2-3-8655bfd59d   4         4         4       71m

NAME                                        REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/tp2-3   Deployment/tp2-3   21%/50%   1         4         4          71m
```
> kubectl top pods
```
NAME                     CPU(cores)   MEMORY(bytes)
tp2-3-8655bfd59d-4sr2l   0m           21Mi
tp2-3-8655bfd59d-gj7zw   0m           21Mi
tp2-3-8655bfd59d-hszjj   0m           21Mi
tp2-3-8655bfd59d-xsd76   0m           21Mi
```

Si bien el uso bajó y está por debajo del target, todavía kubernetes no ha reducido la cantidad de pods. Esto pasará si el uso promedio de la memoria continua estando debajo del target del 50% por unos minutos.

Luego de unos minutos, efectivamente podemos ver como se redujo a un solo pod en el deployment:

> kubectl get all
```
NAME                         READY   STATUS    RESTARTS   AGE
pod/tp2-3-8655bfd59d-hszjj   1/1     Running   0          82m

NAME                 TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
service/kubernetes   ClusterIP      10.124.0.1      <none>        443/TCP          5d23h
service/tp2-3        LoadBalancer   10.124.13.120   34.75.58.1    4001:31781/TCP   82m

NAME                    READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/tp2-3   1/1     1            1           82m

NAME                               DESIRED   CURRENT   READY   AGE
replicaset.apps/tp2-3-8655bfd59d   1         1         1       82m

NAME                                        REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
horizontalpodautoscaler.autoscaling/tp2-3   Deployment/tp2-3   21%/50%   1         4         1          82m
```



