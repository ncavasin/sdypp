# TP2 - Ejercicio 4 - Punto A (Centralizado)
Ejercicio realizado en Nodejs y TypeScript.

## Requisitos
Tener **Node** y **NPM** instalados (puede correr los comandos `node -v` y `npm -v` para saber si los tiene instalados o no).

## Ejecutar el programa
### **Local**
Para ejecutar el programa primero hay que instalar las dependencias ejecutando el comando `npm install`.
Luego, hay que elegir en que entorno ejecutar el programa: **Desarrollo** o **Producción**.

- Para desarrollo: `npm run dev`
- Para produccion:
   1. `npm run build`
   2. `npm run start`

### **Docker**
Para dockerizar la aplicación se necesita realizar los siguientes pasos:
1. Crear la imagen. Hay que tener en cuenta el path del docker file del ejercicio. En el caso de estar posicionado en el mismo directorio del docker file simplemente reemplazar el path por un punto (.)
>  $ docker build -t sdypp-tp2-4-a .

2. Crear el container:
> $ docker run -d -p 4001:4001 --name sdypp-tp2-4-a sdypp-tp2-4-a

### Kubernetes
Para ejecutar la app en k8s hay que aplicar todos los manifiestos dentro de la carpeta `/k8s` con el siguiente comando:

> $ kubectl apply -f `./k8s/`
