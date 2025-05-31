provider "aws" {
  region = var.region
}

resource "aws_iam_role" "eb_instance_role" {
  name = "${var.app_name}-instance-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "s3_access_policy" {
  name = "${var.app_name}-s3-access"
  role = aws_iam_role.eb_instance_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject"
        ],
        Resource = "arn:aws:s3:::${var.xml_bucket_name}/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "eb_instance_role_policy" {
  role       = aws_iam_role.eb_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
}

resource "aws_iam_instance_profile" "eb_instance_profile" {
  name = "${var.app_name}-instance-profile"
  role = aws_iam_role.eb_instance_role.name
}


resource "aws_s3_bucket" "app_bucket" {
  bucket        = var.bucket_name
  force_destroy = true
}

resource "aws_elastic_beanstalk_application" "app" {
  name        = var.app_name
  description = "Managed by Terraform"
}

resource "aws_elastic_beanstalk_environment" "env" {
  name                = var.env_name
  application         = aws_elastic_beanstalk_application.app.name
  solution_stack_name = "64bit Amazon Linux 2023 v4.5.2 running Corretto 17"

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t4g.medium"
  }


  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = aws_iam_instance_profile.eb_instance_profile.name
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "ENVIRONMENT"
    value     = "production"
  }
}

