
/* resource "google_compute_network" "vpc_network" {
  name = "terraform-network001"
} */

# [STEP 1] - Create firewall rule
resource "google_compute_firewall" "ssh" {
  name    = "ssh"
  network = "default"
  #network = google_compute_network.vpc_network.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["externalssh"]
}

resource "google_compute_firewall" "webserver" {
  name    = "ws"
  network = "default"
  allow {
    protocol = "tcp"
    ports    = ["80","443"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["webserver"]
}

# [STEP 3] - Create a public IP for the instance
resource "google_compute_address" "static" {
  name = "instance_public_IP"
  project = var.project
  region = var.region
  depends_on = [ google_compute_firewall.ssh ]
}

resource "google_compute_instance" "dev" {
  name         = var.vm_name
  machine_type = "f1-micro"
  zone         = var.zone
  tags         = ["externalssh","webserver"]
  boot_disk {
    initialize_params {
      image = "centos-cloud/centos-7"
    }
  }
  network_interface {
    network = "default"
    access_config {
      nat_ip = google_compute_address.static.address
    }
  }
  provisioner "remote-exec" {
    connection {
      host        = google_compute_address.static.address
      type        = "ssh"
      user        = var.user
      timeout     = "500s"
      private_key = file(var.privatekeypath)
    }
    inline = [
      "sudo yum -y install epel-release",
      "sudo yum -y install nginx",
      "sudo systemctl start nginx",
      "sudo nginx -v"
    ]
  }
  # Ensure firewall rule is provisioned before server, so that SSH doesn't fail.
  #depends_on = [ google_compute_firewall.ssh, google_compute_firewall.webserver ]

  metadata = {
    ssh-keys = "${var.user}:${file(var.publickeypath)}"
  }
}

# [STEP 2] - Creamos la VM

/* resource "google_compute_instance" "vm_instance" {
  count = 4
  name         = "terraform-instance-${count.index}"
  machine_type = "f1-micro"

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-9"
    }
  }

  network_interface {
    #network       = google_compute_network.vpc_network.name
    network       = "default"
    access_config {
    }
  }

  tags = ["foo-${count.index}", "bar"]
}

resource "google_compute_instance" "vm_instance" {
  count = 4
  name         = "terraform-instance-${count.index}"
  machine_type = "f1-micro"
  region          = "europe-west4"
  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-9"
    }
  }

  network_interface {
    network       = google_compute_network.vpc_network.name
    access_config {
    }
  }

  tags = ["foo-${count.index}", "bar"]
} */