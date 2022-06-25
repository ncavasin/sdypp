output "ippublica" {
    value = "ssh -o StrictHostKeyChecking=no -i ${var.privatekeypath} ${var.user}@${google_compute_address.static.address}"
}

output "nombrevm" {
    value = google_compute_instance.dev.name
}
