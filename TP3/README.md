## Prerequisitos

### Service Account credentials

Es necesario contar con un archivo .json que permita autenticarse contra GCP para poder obtener acceso
a la cuenta destino donde se creará la infraestructura.

!cerrar idea!

### SSH key pair

Para poder ingresar a la instancia que será creada, se necesita contar con un par de claves SSH. La clave pública de 
este par será utilizada durante la creación de la instancia para que una vez levantada podamos accederla.

Proceso de generación de claves:

``ssh-keygen -t rsa -f google_compute_engine``: crea el par de claves **SIN PASSPHRASE**. Caso contrario, fallará el proceso.

``mkdir ssh_keys``: crea el directorio donde TF buscará las claves.

``mv google_compute_engine* /ssh_keys``: mueve las claves creadas al directorio donde TF las buscará.


### Init TF repository

Ejecutar el comando ``terraform init`` para descargar las dependencias definidas en el archivo ``00-providers``.

```bash
~terraform init

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

Una vez inicializado descargadas todas las dependencias necesarias nuestro repositorio de terraform se encuentra en condiciones de poder crear y destruir instancias según lo definido en los diferentes archivos.

Para ello, primero debemos ejecutar el comando ``terraform plan`` que se encargará de determinar el estado final de los recursos que nuestro repositorio declara y luego lo compara con el estado actual del mismo para mostrar claramente el *plan* de ejecución.

```bash
~terraform plan

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
~terraform apply 

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
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 304/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 320/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 336/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 352/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 368/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 384/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 400/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 416/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 432/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 448/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 464/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 480/3318
google_compute_instance.dev (remote-exec): google-cloud-: [#            ] 496/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 512/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 528/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 544/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 560/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 576/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 592/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 608/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 624/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 640/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 656/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 672/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 688/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 704/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 720/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 736/3318
google_compute_instance.dev (remote-exec): google-cloud-: [##           ] 752/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 768/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 784/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 800/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 816/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 832/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 848/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 864/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 880/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 896/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 912/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 928/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 944/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 960/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 976/3318
google_compute_instance.dev (remote-exec): google-cloud-: [###          ] 992/3318
google_compute_instance.dev (remote-exec): google-cloud: [###          ] 1008/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1024/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1040/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1056/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1072/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1088/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1104/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1120/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1136/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1152/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1168/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1184/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1200/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1216/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1232/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1248/3318
google_compute_instance.dev (remote-exec): google-cloud: [####         ] 1264/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1280/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1296/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1312/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1328/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1344/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1360/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1376/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1392/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1408/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1424/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1440/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1456/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1472/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1488/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1504/3318
google_compute_instance.dev (remote-exec): google-cloud: [#####        ] 1520/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1536/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1552/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1568/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1584/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1600/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1616/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1632/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1648/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1664/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1680/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1696/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1712/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1728/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1744/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1760/3318
google_compute_instance.dev (remote-exec): google-cloud: [######       ] 1776/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1792/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1808/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1824/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1840/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1856/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1872/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1888/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1904/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1920/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1936/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1952/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1968/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 1984/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 2000/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 2016/3318
google_compute_instance.dev (remote-exec): google-cloud: [#######      ] 2032/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2048/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2064/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2080/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2096/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2112/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2128/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2144/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2160/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2176/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2192/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2208/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2224/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2240/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2256/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2272/3318
google_compute_instance.dev (remote-exec): google-cloud: [########     ] 2288/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2304/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2320/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2336/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2352/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2368/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2384/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2400/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2416/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2432/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2448/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2464/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2480/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2496/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2512/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2528/3318
google_compute_instance.dev (remote-exec): google-cloud: [#########    ] 2544/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2560/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2576/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2592/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2608/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2624/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2640/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2656/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2672/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2688/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2704/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2720/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2736/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2752/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2768/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2784/3318
google_compute_instance.dev (remote-exec): google-cloud: [##########   ] 2800/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2816/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2832/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2848/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2864/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2880/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2896/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2912/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2928/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2944/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2960/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2976/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 2992/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 3008/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 3024/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 3040/3318
google_compute_instance.dev (remote-exec): google-cloud: [###########  ] 3056/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3072/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3088/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3104/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3120/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3136/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3152/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3168/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3184/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3200/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3216/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3232/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3248/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3264/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3280/3318
google_compute_instance.dev (remote-exec): google-cloud: [############ ] 3296/3318
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

La conexión a la instancia creada será a través de SSH (autenticándonos de manera automática con las claves creadas en la etapa de Prerequisitos) ejecutando el comando ``ssh -o StrictHostKeyChecking=no -i ./ssh_keys/google_compute_engine ncavasin@34.73.33.155``.

Como se puede observar, se agrega la IP pública de la instancia creada como host conocido y luego accedemos a su shell.

![](https://raw.githubusercontent.com/ncavasin/sdypp/main/TP3/imgs/instance_ssh_access.png)

La VM está ahora a nuestra dispoción.


## Instalación de paquetes




## Comparativa descarga imagen ISO de Ubuntu

Para descargar la última versión de Ubuntu desktop se debe utilizar el comando``wget https://releases.ubuntu.com/20.04/ubuntu-20.04.4-desktop-amd64.iso ``.

Local:




Instancia EC2:


## Copiado de archivos





