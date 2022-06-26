
# [STEP 1] - Create firewall rule
resource "google_compute_firewall" "ssh" {
  name    = "ssh"
  network = "default"

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
  name = "publicip"
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

  metadata = {
    ssh-keys = "${var.user}:${file(var.publickeypath)}"
  }
}