## Prerequisitos

### Service Account credentials

Es necesario contar con un archivo .json que permita autenticarse contra GCP para poder obtener acceso
a la cuenta destino donde se creará la infraestructura.

!cerrar idea!

### SSH key pair

Para poder ingresar a la instancia que será creada, se necesita contar con un par de claves SSH. La clave pública de 
este par será utilizada durante la creación de la instancia para que una vez levantada podamos accederla.

Proceso de generación de claves:

``ssh-keygen -t rsa -f google_compute_engine``: crea el par de claves.

``mkdir ssh_keys``: crea el directorio donde TF buscará las claves.

``mv google_compute_engine* /ssh_keys``: mueve las claves creadas al directorio donde TF las buscará.


### Init TF repository

Ejecutar el comando ``terraform init`` para descargar las dependencias definidas en el archivo ``00-providers``.


    
