variable "region" {
  description = "AWS region to deploy to"
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Elastic Beanstalk application name"
  type        = string
}

variable "env_name" {
  description = "Elastic Beanstalk environment name"
  type        = string
}

variable "bucket_name" {
  description = "S3 bucket to store application versions"
  type        = string
}

variable "xml_bucket_name" {
  description = "S3 bucket to store the XML files"
  type        = string
}
