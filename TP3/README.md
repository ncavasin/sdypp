## Prerequisitos

### Service Account credentials

Para poder desplegar infraestructura utilizando Terraform es necesario contar con acceso a una cuenta asociada a algún Cloud provider tal como AWS, GCP, Azure, etc. En este caso, utilizaremos GCP debido a que otorga 300 USD de prueba y es más que suficiente para realizar lo que el presente trabajo demanda.

Con el fin de poder autenticarse contra GCP es necesario contar las credenciales en formato ``.json`` de una Service Account asociada a un proyecto. Esta Service Account debe tener permisos de creación y edición sobre la API de *Compute Engine* y a su vez esta debe habilitarse manualmente.

> Si alguno de los requisitos no se cumple, los comandos ejecutados por Terraform fallarán.

Por motivos de seguridad, dichas credenciales no serán publicadas en el presente repositorio. Sin embargo, si se desea acceder a las mismas puede contactar a cualquier miembro del equipo.


### SSH key pair

Para poder ingresar a la instancia que será creada se necesita contar con un par de claves SSH. La clave pública de este par será utilizada durante la creación de la instancia para que una vez levantada podamos accederla.

Proceso de generación de claves:

``ssh-keygen -t rsa -f google_compute_engine``: crea el par de claves **SIN PASSPHRASE**. Caso contrario, fallará el proceso.

``mkdir ssh_keys``: crea el directorio donde Terraform buscará las claves.

``mv google_compute_engine* /ssh_keys``: mueve las claves creadas al directorio donde Terraform las buscará.


### Descargar dependencias del provider

Ejecutar el comando ``terraform init`` para descargar las dependencias definidas en el archivo ``00-providers``.

```bash
$ terraform init

Initializing the backend...

Initializing provider plugins...
- Finding hashicorp/google versions matching "3.5.0"...
- Installing hashicorp/google v3.5.0...
- Installed hashicorp/google v3.5.0 (signed by HashiCorp)

Terraform has created a lock file .terraform.lock.hcl to record the provider
selections it made above. Include this file in your version control repository
so that Terraform can guarantee to make the same selections by default when
you run "terraform init" in the future.

Terraform has been successfully initialized!

You may now begin working with Terraform. Try running "terraform plan" to see
any changes that are required for your infrastructure. All Terraform commands
should now work.

If you ever set or change modules or backend configuration for Terraform,
rerun this command to reinitialize your working directory. If you forget, other
commands will detect it and remind you to do so if necessary.
```

## Creación de la instancia

Una vez descargadas todas las dependencias necesarias, nuestro repositorio se encuentra en condiciones de poder crear y destruir instancias utlizando Terraform según lo definido en los diferentes archivos.

Para ello, primero debemos ejecutar el comando ``terraform plan``. Este comando se encarga de determinar el estado final de los recursos que nuestro repositorio declara y luego lo compara con el estado actual del mismo para identificar el *plan* de ejecución que nos permitirá alcanzar dicho estado.

```bash
$ terraform plan

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
+ create

Terraform will perform the following actions:

# google_compute_address.static will be created
+ resource "google_compute_address" "static" {
    + address            = (known after apply)
    + address_type       = "EXTERNAL"
    + creation_timestamp = (known after apply)
    + id                 = (known after apply)
    + name               = "publicip"
    + network_tier       = (known after apply)
    + project            = "sdypp-352002"
    + purpose            = (known after apply)
    + region             = "us-east1"
    + self_link          = (known after apply)
    + subnetwork         = (known after apply)
    + users              = (known after apply)
    }

# google_compute_firewall.ssh will be created
+ resource "google_compute_firewall" "ssh" {
    + creation_timestamp = (known after apply)
    + destination_ranges = (known after apply)
    + direction          = (known after apply)
    + id                 = (known after apply)
    + name               = "ssh"
    + network            = "default"
    + priority           = 1000
    + project            = (known after apply)
    + self_link          = (known after apply)
    + source_ranges      = [
        + "0.0.0.0/0",
        ]
    + target_tags        = [
        + "externalssh",
        ]

    + allow {
        + ports    = [
            + "22",
            ]
        + protocol = "tcp"
        }
    }

# google_compute_firewall.webserver will be created
+ resource "google_compute_firewall" "webserver" {
    + creation_timestamp = (known after apply)
    + destination_ranges = (known after apply)
    + direction          = (known after apply)
    + id                 = (known after apply)
    + name               = "ws"
    + network            = "default"
    + priority           = 1000
    + project            = (known after apply)
    + self_link          = (known after apply)
    + source_ranges      = [
        + "0.0.0.0/0",
        ]
    + target_tags        = [
        + "webserver",
        ]

    + allow {
        + ports    = [
            + "80",
            + "443",
            ]
        + protocol = "tcp"
        }
    }

# google_compute_instance.dev will be created
+ resource "google_compute_instance" "dev" {
    + can_ip_forward       = false
    + cpu_platform         = (known after apply)
    + deletion_protection  = false
    + guest_accelerator    = (known after apply)
    + id                   = (known after apply)
    + instance_id          = (known after apply)
    + label_fingerprint    = (known after apply)
    + machine_type         = "f1-micro"
    + metadata             = {
        + "ssh-keys" = <<-EOT
                ncavasin:ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQC6IpMSJyCJLVKua/hc3PZ1prGuPdBa1SKZxUxpgqH5qqHWA9PUaZwDW+bon+L14A2f1Zolh5KUyWf+4N0I3RMI/WCczjqJFGcx6ILIe4fZwkqLL21Z31HU8D5IBZ05kuKgGul48D0Dj5g8P4kR5PN6tPp4oS8zt32YYPqeSW9qMEDMKDcuF4gj9Gr8+QaChdmg3oBRIvS0/C4bOnor7uC7xdLB2OAVcunJvxvC1qYLI+LE18x/gYZ8AmMK/DnPb5b6TkfpsaKkSo05xAlmk2hIZwaWOsfvAMvmxWyT45tTmxpBtrTylO70M++uMiHewMr/c4EjGS9K7mckMPWyVb7Rizqomd83orDQaulrnipkhtU0DY/Wcvw5oXy0HTsZqCnQoH5xh/JpiUwOpH2LjjmmXSolc0wR4dGSe+OSmcxXAIKA9yAaiW+qR/eg4a6PS/2P78lMW4v7CgoJUtaigqv7VxUxT4rAMhupjYy4cPfiahXpC3J3TYCvwepeVkfP5m0= nico@arch
            EOT
        }
    + metadata_fingerprint = (known after apply)
    + min_cpu_platform     = (known after apply)
    + name                 = "vm-public-bastion"
    + project              = (known after apply)
    + self_link            = (known after apply)
    + tags                 = [
        + "externalssh",
        + "webserver",
        ]
    + tags_fingerprint     = (known after apply)
    + zone                 = "us-east1-b"

    + boot_disk {
        + auto_delete                = true
        + device_name                = (known after apply)
        + disk_encryption_key_sha256 = (known after apply)
        + kms_key_self_link          = (known after apply)
        + mode                       = "READ_WRITE"
        + source                     = (known after apply)

        + initialize_params {
            + image  = "centos-cloud/centos-7"
            + labels = (known after apply)
            + size   = (known after apply)
            + type   = (known after apply)
            }
        }

    + network_interface {
        + name               = (known after apply)
        + network            = "default"
        + network_ip         = (known after apply)
        + subnetwork         = (known after apply)
        + subnetwork_project = (known after apply)

        + access_config {
            + nat_ip       = (known after apply)
            + network_tier = (known after apply)
            }
        }

    + scheduling {
        + automatic_restart   = (known after apply)
        + on_host_maintenance = (known after apply)
        + preemptible         = (known after apply)

        + node_affinities {
            + key      = (known after apply)
            + operator = (known after apply)
            + values   = (known after apply)
            }
        }
    }

Plan: 4 to add, 0 to change, 0 to destroy.

Changes to Outputs:
+ ippublica = (known after apply)
+ nombrevm  = "vm-public-bastion"
```

Si estamos de acuerdo con el plan definido, indicamos a terraform que deseamos que aplique dicho plan con el comando ``terraform apply``.


```bash
$ terraform apply 

Plan: 4 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + ippublica = (known after apply)
  + nombrevm  = "vm-public-bastion"

Do you want to perform these actions?
  Terraform will perform the actions described above.
  Only 'yes' will be accepted to approve.

  Enter a value: yes

google_compute_firewall.ssh: Creating...
google_compute_firewall.webserver: Creating...
google_compute_firewall.ssh: Creation complete after 8s [id=projects/sdypp-352002/global/firewalls/ssh]
google_compute_address.static: Creating...
google_compute_firewall.webserver: Creation complete after 8s [id=projects/sdypp-352002/global/firewalls/ws]
google_compute_address.static: Creation complete after 2s [id=projects/sdypp-352002/regions/us-east1/addresses/publicip]
google_compute_instance.dev: Creating...
google_compute_instance.dev: Still creating... [10s elapsed]
google_compute_instance.dev: Provisioning with 'remote-exec'...
google_compute_instance.dev (remote-exec): Connecting to remote host via SSH...
google_compute_instance.dev (remote-exec):   Host: 34.73.33.155
google_compute_instance.dev (remote-exec):   User: ncavasin
google_compute_instance.dev (remote-exec):   Password: false
google_compute_instance.dev (remote-exec):   Private key: true
google_compute_instance.dev (remote-exec):   Certificate: false
google_compute_instance.dev (remote-exec):   SSH Agent: true
google_compute_instance.dev (remote-exec):   Checking Host Key: false
google_compute_instance.dev (remote-exec):   Target Platform: unix
google_compute_instance.dev: Still creating... [20s elapsed]
google_compute_instance.dev (remote-exec): Connecting to remote host via SSH...
google_compute_instance.dev (remote-exec):   Host: 34.73.33.155
google_compute_instance.dev (remote-exec):   User: ncavasin
google_compute_instance.dev (remote-exec):   Password: false
google_compute_instance.dev (remote-exec):   Private key: true
google_compute_instance.dev (remote-exec):   Certificate: false
google_compute_instance.dev (remote-exec):   SSH Agent: true
google_compute_instance.dev (remote-exec):   Checking Host Key: false
google_compute_instance.dev (remote-exec):   Target Platform: unix
google_compute_instance.dev: Still creating... [30s elapsed]
google_compute_instance.dev (remote-exec): Connected!
google_compute_instance.dev (remote-exec): Loaded plugins: fastestmirror
google_compute_instance.dev (remote-exec): Determining fastest mirrors
google_compute_instance.dev (remote-exec): epel/x86_64/meta |  21 kB     00:00
google_compute_instance.dev (remote-exec):  * base: mirror.wdc1.us.leaseweb.net
google_compute_instance.dev (remote-exec):  * epel: mirror.umd.edu
google_compute_instance.dev (remote-exec):  * extras: mirror.centos.iad1.serverforge.org
google_compute_instance.dev (remote-exec):  * updates: centos.mirror.constant.com
google_compute_instance.dev (remote-exec): base             | 3.6 kB     00:00
google_compute_instance.dev (remote-exec): epel             | 4.7 kB     00:00
google_compute_instance.dev (remote-exec): extras           | 2.9 kB     00:00
google_compute_instance.dev (remote-exec): google-cloud-sdk | 1.4 kB     00:00
google_compute_instance.dev (remote-exec): google-compute-e | 1.4 kB     00:00
google_compute_instance.dev (remote-exec): updates          | 2.9 kB     00:00
google_compute_instance.dev (remote-exec): (1/9): base/7/x86_ | 153 kB   00:00
google_compute_instance.dev (remote-exec): (2/9): base/7/x86_ | 6.1 MB   00:00
google_compute_instance.dev (remote-exec): (3/9): epel/x8 20% | 6.2 MB   --:-- ETA
google_compute_instance.dev (remote-exec): (3/9): epel/x86_64 |  96 kB   00:00
google_compute_instance.dev (remote-exec): (4/9): epel/x86_64 | 7.0 MB   00:00
google_compute_instance.dev (remote-exec): (5/9): epel/x86_64 | 1.1 MB   00:00
google_compute_instance.dev (remote-exec): (6/9): extras/ 46% |  14 MB   00:01 ETA
google_compute_instance.dev (remote-exec): (6/9): google-clou | 447 kB   00:00
google_compute_instance.dev (remote-exec): (7/9): extras/7/x8 | 247 kB   00:00
google_compute_instance.dev (remote-exec): (8/9): google-comp | 3.8 kB   00:00
google_compute_instance.dev: Still creating... [40s elapsed]
google_compute_instance.dev (remote-exec): (9/9): updates 55% |  17 MB   00:01 ETA
google_compute_instance.dev (remote-exec): (9/9): updates 68% |  21 MB   00:01 ETA
google_compute_instance.dev (remote-exec): (9/9): updates 92% |  28 MB   00:00 ETA
google_compute_instance.dev (remote-exec): (9/9): updates/7/x |  16 MB   00:01
google_compute_instance.dev (remote-exec): google-cloud-s: [              ] 1/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 16/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 32/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 48/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 64/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 80/3318
google_compute_instance.dev (remote-exec): google-cloud-: [              ] 96/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 112/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 128/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 144/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 160/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 176/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 192/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 208/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 224/3318
google_compute_instance.dev (remote-exec): google-cloud-: [             ] 240/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 256/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 272/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 288/3318
.
.
.
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 864/3318
.
.
.
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1424/3318
.
.
.
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2528/3318
.
.
.
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3312/3318
google_compute_instance.dev (remote-exec): google-cloud-sdk              3318/3318
google_compute_instance.dev (remote-exec): google-compute-: [#              ] 1/10
google_compute_instance.dev (remote-exec): google-compute-: [###            ] 2/10
google_compute_instance.dev (remote-exec): google-compute-: [####           ] 3/10
google_compute_instance.dev (remote-exec): google-compute-: [######         ] 4/10
google_compute_instance.dev (remote-exec): google-compute-: [#######        ] 5/10
google_compute_instance.dev (remote-exec): google-compute-: [#########      ] 6/10
google_compute_instance.dev (remote-exec): google-compute-: [##########     ] 7/10
google_compute_instance.dev (remote-exec): google-compute-: [############   ] 8/10
google_compute_instance.dev (remote-exec): google-compute-: [#############  ] 9/10
google_compute_instance.dev (remote-exec): google-compute-engine             10/10
google_compute_instance.dev (remote-exec): Package epel-release-7-14.noarch already installed and latest version
google_compute_instance.dev (remote-exec): Nothing to do
google_compute_instance.dev (remote-exec): Loaded plugins: fastestmirror
google_compute_instance.dev (remote-exec): Loading mirror speeds from cached hostfile
google_compute_instance.dev (remote-exec):  * base: mirror.wdc1.us.leaseweb.net
google_compute_instance.dev (remote-exec):  * epel: mirror.umd.edu
google_compute_instance.dev (remote-exec):  * extras: mirror.centos.iad1.serverforge.org
google_compute_instance.dev (remote-exec):  * updates: centos.mirror.constant.com
google_compute_instance.dev (remote-exec): Resolving Dependencies
google_compute_instance.dev (remote-exec): --> Running transaction check
google_compute_instance.dev (remote-exec): ---> Package nginx.x86_64 1:1.20.1-9.el7 will be installed
google_compute_instance.dev (remote-exec): --> Processing Dependency: nginx-filesystem = 1:1.20.1-9.el7 for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libcrypto.so.1.1(OPENSSL_1_1_0)(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libssl.so.1.1(OPENSSL_1_1_0)(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libssl.so.1.1(OPENSSL_1_1_1)(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: nginx-filesystem for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: openssl for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: redhat-indexhtml for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libcrypto.so.1.1()(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libprofiler.so.0()(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Processing Dependency: libssl.so.1.1()(64bit) for package: 1:nginx-1.20.1-9.el7.x86_64
google_compute_instance.dev (remote-exec): --> Running transaction check
google_compute_instance.dev (remote-exec): ---> Package centos-indexhtml.noarch 0:7-9.el7.centos will be installed
google_compute_instance.dev (remote-exec): ---> Package gperftools-libs.x86_64 0:2.6.1-1.el7 will be installed
google_compute_instance.dev (remote-exec): ---> Package nginx-filesystem.noarch 1:1.20.1-9.el7 will be installed
google_compute_instance.dev (remote-exec): ---> Package openssl.x86_64 1:1.0.2k-25.el7_9 will be installed
google_compute_instance.dev (remote-exec): --> Processing Dependency: make for package: 1:openssl-1.0.2k-25.el7_9.x86_64
google_compute_instance.dev (remote-exec): ---> Package openssl11-libs.x86_64 1:1.1.1k-3.el7 will be installed
google_compute_instance.dev (remote-exec): --> Running transaction check
google_compute_instance.dev (remote-exec): ---> Package make.x86_64 1:3.82-24.el7 will be installed
google_compute_instance.dev (remote-exec): --> Finished Dependency Resolution

google_compute_instance.dev (remote-exec): Dependencies Resolved

google_compute_instance.dev (remote-exec): ========================================
google_compute_instance.dev (remote-exec):  Package
google_compute_instance.dev (remote-exec):        Arch   Version        Repository
google_compute_instance.dev (remote-exec):                                    Size
google_compute_instance.dev (remote-exec): ========================================
google_compute_instance.dev (remote-exec): Installing:
google_compute_instance.dev (remote-exec):  nginx x86_64 1:1.20.1-9.el7 epel 587 k
google_compute_instance.dev (remote-exec): Installing for dependencies:
google_compute_instance.dev (remote-exec):  centos-indexhtml
google_compute_instance.dev (remote-exec):        noarch 7-9.el7.centos base  92 k
google_compute_instance.dev (remote-exec):  gperftools-libs
google_compute_instance.dev (remote-exec):        x86_64 2.6.1-1.el7    base 272 k
google_compute_instance.dev (remote-exec):  make  x86_64 1:3.82-24.el7  base 421 k
google_compute_instance.dev (remote-exec):  nginx-filesystem
google_compute_instance.dev (remote-exec):        noarch 1:1.20.1-9.el7 epel  24 k
google_compute_instance.dev (remote-exec):  openssl
google_compute_instance.dev (remote-exec):        x86_64 1:1.0.2k-25.el7_9
google_compute_instance.dev (remote-exec):                              updates
google_compute_instance.dev (remote-exec):                                   494 k
google_compute_instance.dev (remote-exec):  openssl11-libs
google_compute_instance.dev (remote-exec):        x86_64 1:1.1.1k-3.el7 epel 1.5 M

google_compute_instance.dev (remote-exec): Transaction Summary
google_compute_instance.dev (remote-exec): ========================================
google_compute_instance.dev (remote-exec): Install  1 Package (+6 Dependent packages)

google_compute_instance.dev (remote-exec): Total download size: 3.3 M
google_compute_instance.dev (remote-exec): Installed size: 8.6 M
google_compute_instance.dev (remote-exec): Downloading packages:
google_compute_instance.dev: Still creating... [50s elapsed]
google_compute_instance.dev (remote-exec): (1/7): centos-inde |  92 kB   00:00
google_compute_instance.dev (remote-exec): (2/7): gperftools- | 272 kB   00:00
google_compute_instance.dev (remote-exec): (3/7): make-3.82-2 | 421 kB   00:00
google_compute_instance.dev (remote-exec): (4/7): nginx-1.20. | 587 kB   00:00
google_compute_instance.dev (remote-exec): (6/7): openssl 40% | 1.3 MB   --:-- ETA
google_compute_instance.dev (remote-exec): (5/7): nginx-files |  24 kB   00:00
google_compute_instance.dev (remote-exec): (6/7): openssl11-l | 1.5 MB   00:00
google_compute_instance.dev (remote-exec): (7/7): openssl 94% | 3.1 MB   00:00 ETA
google_compute_instance.dev (remote-exec): (7/7): openssl 99% | 3.3 MB   00:00 ETA
google_compute_instance.dev (remote-exec): (7/7): openssl-1.0 | 494 kB   00:01
google_compute_instance.dev (remote-exec): ----------------------------------------
google_compute_instance.dev (remote-exec): Total      2.1 MB/s | 3.3 MB  00:01
google_compute_instance.dev (remote-exec): Running transaction check
google_compute_instance.dev (remote-exec): Running transaction test
google_compute_instance.dev (remote-exec): Transaction test succeeded
google_compute_instance.dev (remote-exec): Running transaction
google_compute_instance.dev (remote-exec):   Installing : centos-i [         ] 1/7
google_compute_instance.dev (remote-exec):   Installing : centos-i [######   ] 1/7
google_compute_instance.dev (remote-exec):   Installing : centos-i [######## ] 1/7
google_compute_instance.dev (remote-exec):   Installing : centos-indexhtml-7   1/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [         ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#        ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [##       ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [###      ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [####     ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#####    ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [######   ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#######  ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [######## ] 2/7
google_compute_instance.dev (remote-exec):   Installing : 1:openssl11-libs-1   2/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [         ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [#        ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [##       ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [###      ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [####     ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [#####    ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [######   ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [#######  ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3 [######## ] 3/7
google_compute_instance.dev (remote-exec):   Installing : 1:make-3.82-24.el7   3/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [         ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#        ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [##       ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [###      ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [####     ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#####    ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [######   ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [#######  ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openss [######## ] 4/7
google_compute_instance.dev (remote-exec):   Installing : 1:openssl-1.0.2k-2   4/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [         ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [#        ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [##       ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [###      ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [####     ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [#####    ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [######   ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [#######  ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftoo [######## ] 5/7
google_compute_instance.dev (remote-exec):   Installing : gperftools-libs-2.   5/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [         ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [##       ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [###      ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [####     ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [#####    ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [######   ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [#######  ] 6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx-filesystem   6/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [         ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [#        ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [##       ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [###      ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [####     ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [#####    ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [######   ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [#######  ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx- [######## ] 7/7
google_compute_instance.dev (remote-exec):   Installing : 1:nginx-1.20.1-9.e   7/7
google_compute_instance.dev (remote-exec):   Verifying  : 1:nginx-filesystem   1/7
google_compute_instance.dev (remote-exec):   Verifying  : 1:nginx-1.20.1-9.e   2/7
google_compute_instance.dev (remote-exec):   Verifying  : gperftools-libs-2.   3/7
google_compute_instance.dev (remote-exec):   Verifying  : 1:openssl-1.0.2k-2   4/7
google_compute_instance.dev (remote-exec):   Verifying  : 1:make-3.82-24.el7   5/7
google_compute_instance.dev (remote-exec):   Verifying  : 1:openssl11-libs-1   6/7
google_compute_instance.dev (remote-exec):   Verifying  : centos-indexhtml-7   7/7

google_compute_instance.dev (remote-exec): Installed:
google_compute_instance.dev (remote-exec):   nginx.x86_64 1:1.20.1-9.el7

google_compute_instance.dev (remote-exec): Dependency Installed:
google_compute_instance.dev (remote-exec):   centos-indexhtml.noarch 0:7-9.el7.centos
google_compute_instance.dev (remote-exec):   gperftools-libs.x86_64 0:2.6.1-1.el7
google_compute_instance.dev (remote-exec):   make.x86_64 1:3.82-24.el7
google_compute_instance.dev (remote-exec):   nginx-filesystem.noarch 1:1.20.1-9.el7
google_compute_instance.dev (remote-exec):   openssl.x86_64 1:1.0.2k-25.el7_9
google_compute_instance.dev (remote-exec):   openssl11-libs.x86_64 1:1.1.1k-3.el7

google_compute_instance.dev (remote-exec): Complete!
google_compute_instance.dev (remote-exec): nginx version: nginx/1.20.1
google_compute_instance.dev: Creation complete after 55s [id=projects/sdypp-352002/zones/us-east1-b/instances/vm-public-bastion]

Apply complete! Resources: 4 added, 0 changed, 0 destroyed.

Outputs:

ippublica = "ssh -o StrictHostKeyChecking=no -i ./ssh_keys/google_compute_engine ncavasin@34.73.33.155"
nombrevm = "vm-public-bastion"

```

## Conexión a la instancia

Al llegar a este punto la instancia ha sido creada exitosamente y ya puede ser consumida. La conexión a la misma será realizada a través de SSH (autenticándonos de manera automática con las claves creadas en la etapa de Prerequisitos) ejecutando el comando:

``ssh -o StrictHostKeyChecking=no -i ./ssh_keys/google_compute_engine ncavasin@34.73.33.155``.

Como se puede observar, se agrega la IP pública de la instancia creada como host conocido y luego accedemos a su shell.

```bash
$ ssh -o StrictHostChecking=no -i ./ssh_keys/google_compute_engine ncavasin@34.73.33.155
Warning: Permanently added '34.73.33.155' (ED25519) to the list of known hosts.
Last login: Sun Jun 26 22:43:11 2022 from 181.170.115.210
[ncavasin@vm-public-bastion ~]$
```

La VM está ahora a nuestra dispoción.

## Reinicio del sistema

Una vez logueados, procedemos a reiniciar la instancia remotamente. Como es de esperar, la conexión SSH es interrumpida.

```bash
[ncavasin@vm-public-bastion ~]$ sudo reboot now
Connection to 34.73.33.155 closed by remote host.
Connection to 34.73.33.155 closed.
```

Al cabo de unos segundos se vuelve a intentar establecer la conexión con el mismo comando del apartado anterior.

```bash
$ ssh -o StrictHostChecking=no -i ./ssh_keys/google_compute_engine ncavasin@34.73.33.155
Last login: Sun Jun 26 22:45:11 2022 from 181.170.115.210
[ncavasin@vm-public-bastion ~]$
```

La VM responde y vuelve a estar a nuestra merced.

## Instalación de paquetes

Nuevamente adentro del sistema, procedemos a instalar ``wget``:

```bash
[ncavasin@vm-public-bastion ~]$ sudo yum  install wget
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirror.wdc1.us.leaseweb.net
 * epel: mirror.umd.edu
 * extras: mirror.centos.iad1.serverforge.org
 * updates: centos.mirror.constant.com
Resolving Dependencies
--> Running transaction check
---> Package wget.x86_64 0:1.14-18.el7_6.1 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

============================================================================================================================================================================
 Package                               Arch                                    Version                                          Repository                             Size
============================================================================================================================================================================
Installing:
 wget                                  x86_64                                  1.14-18.el7_6.1                                  base                                  547 k

Transaction Summary
============================================================================================================================================================================
Install  1 Package

Total download size: 547 k
Installed size: 2.0 M
Is this ok [y/d/N]: y
Downloading packages:
wget-1.14-18.el7_6.1.x86_64.rpm                                                                                                                      | 547 kB  00:00:00     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  Installing : wget-1.14-18.el7_6.1.x86_64                                                                                                                              1/1 
  Verifying  : wget-1.14-18.el7_6.1.x86_64                                                                                                                              1/1 

Installed:
  wget.x86_64 0:1.14-18.el7_6.1                                                                                                                                             

Complete!
```

Y luego instalamos ``htop``:
```bash
[ncavasin@vm-public-bastion ~]$ sudo yum  install htop
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirror.wdc1.us.leaseweb.net
 * epel: mirror.umd.edu
 * extras: mirror.centos.iad1.serverforge.org
 * updates: centos.mirror.constant.com
Resolving Dependencies
--> Running transaction check
---> Package htop.x86_64 0:2.2.0-3.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

======================================================================================================================================================================================
 Package                                  Arch                                       Version                                           Repository                                Size
======================================================================================================================================================================================
Installing:
 htop                                     x86_64                                     2.2.0-3.el7                                       epel                                     103 k

Transaction Summary
======================================================================================================================================================================================
Install  1 Package

Total download size: 103 k
Installed size: 218 k
Is this ok [y/d/N]: y
Downloading packages:
htop-2.2.0-3.el7.x86_64.rpm                                                                                                                                    | 103 kB  00:00:00     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  Installing : htop-2.2.0-3.el7.x86_64                                                                                                                                            1/1 
  Verifying  : htop-2.2.0-3.el7.x86_64                                                                                                                                            1/1 

Installed:
  htop.x86_64 0:2.2.0-3.el7                                                                                                                                                           

Complete!

```

Los recursos del sistema según ``htop``:

![](https://raw.githubusercontent.com/ncavasin/sdypp/main/TP3/imgs/instance_htop_resources.png)



## Comparativa descarga imagen ISO de Ubuntu

Para comparar las velocidades entre la instancia de GCP y una pc fuera del cloud de GCP, se descargará la última versión desktop de Ubuntu a través del comando ``wget``.

### Local
```bash
$ wget https://releases.ubuntu.com/20.04/ubuntu-20.04.4-desktop-amd64.iso
--2022-06-26 20:15:16--  https://releases.ubuntu.com/20.04/ubuntu-20.04.4-desktop-amd64.iso
Loaded CA certificate '/etc/ssl/certs/ca-certificates.crt'
Resolving releases.ubuntu.com (releases.ubuntu.com)... 91.189.91.124, 185.125.190.40, 91.189.91.123, ...
Connecting to releases.ubuntu.com (releases.ubuntu.com)|91.189.91.124|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 3379068928 (3.1G) [application/x-iso9660-image]
Saving to: ‘ubuntu-20.04.4-desktop-amd64.iso’

ubuntu-20.04.4-desktop-amd64.iso              100%[===============================================================================================>]   3.15G  7.95MB/s    in 6m 37s  

2022-06-26 20:21:55 (8.11 MB/s) - ‘ubuntu-20.04.4-desktop-amd64.iso’ saved [3379068928/3379068928]
```


Como se puede observar, el tiempo de descarga es de 6 minutos y 37 segundos con una velocidad de descarga de 7.95 MB/s.

### Instancia GCP
```bash
[ncavasin@vm-public-bastion ~]$ wget https://releases.ubuntu.com/20.04/ubuntu-20.04.4-desktop-amd64.iso
--2022-06-26 23:12:22--  https://releases.ubuntu.com/20.04/ubuntu-20.04.4-desktop-amd64.iso
Resolving releases.ubuntu.com (releases.ubuntu.com)... 91.189.91.123, 91.189.91.124, 185.125.190.37, ...
Connecting to releases.ubuntu.com (releases.ubuntu.com)|91.189.91.123|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 3379068928 (3.1G) [application/x-iso9660-image]
Saving to: ‘ubuntu-20.04.4-desktop-amd64.iso’

100%[==========================================================================================================================================>] 3,379,068,928 36.0MB/s   in 87s    

2022-06-26 23:13:50 (36.9 MB/s) - ‘ubuntu-20.04.4-desktop-amd64.iso’ saved [3379068928/3379068928]
```
Mientras que en la instancia demora unos 87 segundos por poseer una velocidad de descarga de 36 MB/s.

> La velocidad de descarga es increíblemente superior en la cloud de GCP respecto a la de un ISP regular.


## Copiado de archivos

### Local a Instancia

Se crea un archivo llamado ``local_transfer.txt`` con el contenido "prueba local" y luego se lo copia a destino utilizando SSH.

```bash
$ echo "prueba local" > local_transfer.txt
scp -i ./ssh_keys/google_compute_engine local_transfer.txt ncavasin@34.73.33.155:/home/ncavasin/
local_transfer.txt                                                                                                                                  100%   13     0.1KB/s   00:00    
```

Verificación en instancia:
```bash
[ncavasin@vm-public-bastion ~]$ ls -al
total 3299888
drwx------. 4 ncavasin ncavasin        155 Jun 26 23:20 .
drwxr-xr-x. 3 root     root             22 Jun 26 22:43 ..
-rw-r--r--. 1 ncavasin ncavasin         18 Nov 24  2021 .bash_logout
-rw-r--r--. 1 ncavasin ncavasin        193 Nov 24  2021 .bash_profile
-rw-r--r--. 1 ncavasin ncavasin        231 Nov 24  2021 .bashrc
drwx------. 3 ncavasin ncavasin         18 Jun 26 23:08 .config
-rw-r--r--. 1 ncavasin ncavasin         13 Jun 26 23:20 local_transfer.txt
drwx------. 2 ncavasin ncavasin         29 Jun 26 22:43 .ssh
-rw-rw-r--. 1 ncavasin ncavasin 3379068928 Feb 23 09:10 ubuntu-20.04.4-desktop-amd64.iso
[ncavasin@vm-public-bastion ~]$ cat local_transfer.txt 
prueba local
[ncavasin@vm-public-bastion ~]$ 
```

Como se puede observar, la transferencia del archivo fué exitosa.

## Clonación de la infraestructura

La única manera de clonar nuestra infraestructura, en esta caso una única instancia, es utilizando el TF State.

Cada vez que se ejecuta el comando ``terraform apply`` se modifica el estado del repositorio de manera acorde a lo definido en sus archivos. Estas modificaciones se reflejan en el archivo ``terraform.state`` el cual siempre almacena el estado actual del mismo. Es decir, es algo así como un *snapshot* de nuestra infraestructura.
Por lo tanto, para poder clonarla necesitamos dicho estado. El proceso a realizar es similar a la restauración de una base de datos a través de un backup.

El proceso de clonado sencillo. Se deben realizar todos los pasos mencionados en Prerequisitos, es decir: poseer credenciales de una Service Account, poseer un par de claves SSH y haber inicializado el repositorio de terraform.

Llegado a dicho punto, el comando a ejecutar ahora es ``terraform plan -f terraform.state``.

> Nota: es conveniente realizar un backup periódico del TF State y almacenarlo en un bucket para que siempre esté disponible.


## Destrucción de la infraestructura

Para finalizar, destruimos la infraestructura desplegada con el comando ``terraform destroy``.

```bash
$ terraform destroy
google_compute_firewall.webserver: Refreshing state... [id=projects/sdypp-352002/global/firewalls/ws]
google_compute_firewall.ssh: Refreshing state... [id=projects/sdypp-352002/global/firewalls/ssh]
google_compute_address.static: Refreshing state... [id=projects/sdypp-352002/regions/us-east1/addresses/publicip]
google_compute_instance.dev: Refreshing state... [id=projects/sdypp-352002/zones/us-east1-b/instances/vm-public-bastion]

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  - destroy

Terraform will perform the following actions:

  # google_compute_address.static will be destroyed
  - resource "google_compute_address" "static" {
      - address            = "34.73.33.155" -> null
      - address_type       = "EXTERNAL" -> null
      - creation_timestamp = "2022-06-26T15:42:37.709-07:00" -> null
      - id                 = "projects/sdypp-352002/regions/us-east1/addresses/publicip" -> null
      - name               = "publicip" -> null
      - network_tier       = "PREMIUM" -> null
      - project            = "sdypp-352002" -> null
      - region             = "us-east1" -> null
      - self_link          = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/regions/us-east1/addresses/publicip" -> null
      - users              = [
          - "https://www.googleapis.com/compute/v1/projects/sdypp-352002/zones/us-east1-b/instances/vm-public-bastion",
        ] -> null
    }

  # google_compute_firewall.ssh will be destroyed
  - resource "google_compute_firewall" "ssh" {
      - creation_timestamp      = "2022-06-26T15:42:29.383-07:00" -> null
      - destination_ranges      = [] -> null
      - direction               = "INGRESS" -> null
      - disabled                = false -> null
      - enable_logging          = false -> null
      - id                      = "projects/sdypp-352002/global/firewalls/ssh" -> null
      - name                    = "ssh" -> null
      - network                 = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/global/networks/default" -> null
      - priority                = 1000 -> null
      - project                 = "sdypp-352002" -> null
      - self_link               = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/global/firewalls/ssh" -> null
      - source_ranges           = [
          - "0.0.0.0/0",
        ] -> null
      - source_service_accounts = [] -> null
      - source_tags             = [] -> null
      - target_service_accounts = [] -> null
      - target_tags             = [
          - "externalssh",
        ] -> null

      - allow {
          - ports    = [
              - "22",
            ] -> null
          - protocol = "tcp" -> null
        }
    }

  # google_compute_firewall.webserver will be destroyed
  - resource "google_compute_firewall" "webserver" {
      - creation_timestamp      = "2022-06-26T15:42:29.411-07:00" -> null
      - destination_ranges      = [] -> null
      - direction               = "INGRESS" -> null
      - disabled                = false -> null
      - enable_logging          = false -> null
      - id                      = "projects/sdypp-352002/global/firewalls/ws" -> null
      - name                    = "ws" -> null
      - network                 = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/global/networks/default" -> null
      - priority                = 1000 -> null
      - project                 = "sdypp-352002" -> null
      - self_link               = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/global/firewalls/ws" -> null
      - source_ranges           = [
          - "0.0.0.0/0",
        ] -> null
      - source_service_accounts = [] -> null
      - source_tags             = [] -> null
      - target_service_accounts = [] -> null
      - target_tags             = [
          - "webserver",
        ] -> null

      - allow {
          - ports    = [
              - "80",
              - "443",
            ] -> null
          - protocol = "tcp" -> null
        }
    }

  # google_compute_instance.dev will be destroyed
  - resource "google_compute_instance" "dev" {
      - can_ip_forward       = false -> null
      - cpu_platform         = "Intel Haswell" -> null
      - deletion_protection  = false -> null
      - enable_display       = false -> null
      - guest_accelerator    = [] -> null
      - id                   = "projects/sdypp-352002/zones/us-east1-b/instances/vm-public-bastion" -> null
      - instance_id          = "6785385746230763022" -> null
      - label_fingerprint    = "42WmSpB8rSM=" -> null
      - labels               = {} -> null
      - machine_type         = "f1-micro" -> null
      - metadata             = {
          - "ssh-keys" = <<-EOT
                ncavasin:ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQC6IpMSJyCJLVKua/hc3PZ1prGuPdBa1SKZxUxpgqH5qqHWA9PUaZwDW+bon+L14A2f1Zolh5KUyWf+4N0I3RMI/WCczjqJFGcx6ILIe4fZwkqLL21Z31HU8D5IBZ05kuKgGul48D0Dj5g8P4kR5PN6tPp4oS8zt32YYPqeSW9qMEDMKDcuF4gj9Gr8+QaChdmg3oBRIvS0/C4bOnor7uC7xdLB2OAVcunJvxvC1qYLI+LE18x/gYZ8AmMK/DnPb5b6TkfpsaKkSo05xAlmk2hIZwaWOsfvAMvmxWyT45tTmxpBtrTylO70M++uMiHewMr/c4EjGS9K7mckMPWyVb7Rizqomd83orDQaulrnipkhtU0DY/Wcvw5oXy0HTsZqCnQoH5xh/JpiUwOpH2LjjmmXSolc0wR4dGSe+OSmcxXAIKA9yAaiW+qR/eg4a6PS/2P78lMW4v7CgoJUtaigqv7VxUxT4rAMhupjYy4cPfiahXpC3J3TYCvwepeVkfP5m0= nico@arch
            EOT
        } -> null
      - metadata_fingerprint = "IGEY5uDO_38=" -> null
      - name                 = "vm-public-bastion" -> null
      - project              = "sdypp-352002" -> null
      - self_link            = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/zones/us-east1-b/instances/vm-public-bastion" -> null
      - tags                 = [
          - "externalssh",
          - "webserver",
        ] -> null
      - tags_fingerprint     = "Mh9u1hBHiNA=" -> null
      - zone                 = "us-east1-b" -> null

      - boot_disk {
          - auto_delete = true -> null
          - device_name = "persistent-disk-0" -> null
          - mode        = "READ_WRITE" -> null
          - source      = "https://www.googleapis.com/compute/v1/projects/sdypp-352002/zones/us-east1-b/disks/vm-public-bastion" -> null

          - initialize_params {
              - image  = "https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images/centos-7-v20220621" -> null
              - labels = {} -> null
              - size   = 20 -> null
              - type   = "pd-standard" -> null
            }
        }

      - network_interface {
```




