variable "region" {
    type = string
    description = "GCP's region"
    default = "us-east1"
}

variable "zone" {
    type = string
    description = "Region's zone"
    default = "us-east1-b"
}

variable "project" {
    type = string
    description = "Proyect's ID"
    default = "tp3-iac-354414"
}

variable "user" {
    default = "ncavasin"
}
variable "vm_name" {
    default = "vm-public-bastion"
}

variable "privatekeypath" {
    type = string
    default = "./ssh_keys/google_compute_engine"
}
variable "publickeypath" {
    type = string
    default = "./ssh_keys/google_compute_engine.pub"
}