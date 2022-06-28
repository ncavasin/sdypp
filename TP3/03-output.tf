output "ippublica" {
    value = "ssh -o StrictHostKeyChecking=no -i ${var.privatekeypath} ${var.user}@${google_compute_address.static[0].address} \n ssh -o StrictHostKeyChecking=no -i ${var.privatekeypath} ${var.user}@${google_compute_address.static[1].address} "
}
