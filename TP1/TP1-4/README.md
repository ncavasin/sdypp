# TP1 - Ejercicio 4
Ejercicio realizado en Nodejs y TypeScript.

## Requisitos
Tener **Node** y **NPM** instalados (puede correr los comandos `node -v` y `npm -v` para saber si los tiene instalados o no).

## Ejecutar el programa
Para ejecutar el programa primero hay que instalar las dependencias ejecutando el comando `npm install`
Luego, hay que elegir en que entorno ejecutar el programa: **Desarrollo** o **Producción**.

- Para desarrollo: `npm run dev`
- Para produccion:
   - `npm run build`
   - `npm run start`

## Docker
Para dockerizar la aplicación se necesita realizar los siguientes pasos:
1. Crear la imagen. Hay que tener en cuenta el path del docker file del ejercicio. En el caso de estar posicionado en el mismo directorio del docker file simplemente reemplazar el path por un punto (.)
>  $ docker build -t sdypp-tp1-4 .

2. Crear el container:
> $ docker run -d -p 3001:3001 --name ssypp-tp1-4 sdypp-tp1-4