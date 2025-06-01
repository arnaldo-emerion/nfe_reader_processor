#!/bin/bash

set -e

# ───── CONFIGURE THESE VARIABLES ──────────────────────────
APP_NAME="nfe-reader-processor-app"
ENV_NAME="nfe-reader-processor-env"
REGION="us-east-1"
PROFILE="aws-arnaldo"                 # AWS CLI named profile
BUCKET_NAME="nfe-reader-processor-bucket"  # Must already exist & be in correct region
JAR_NAME="nfereader-processor-application-1.0.0.jar"
ZIP_FILE="nfereader-app-$(date +%Y%m%d%H%M%S).zip"
# ──────────────────────────────────────────────────────────

# ───── Step 1: Build the JAR ──────────────────────────────
echo "Building project with Maven..."
mvn clean package -DskipTests

# ───── Step 2: Create Procfile ────────────────────────────
echo "Creating Procfile..."
echo "web: java -jar $JAR_NAME" > Procfile

# ───── Step 3: Package ZIP ────────────────────────────────
echo "Packaging ZIP..."
cd target
zip "../$ZIP_FILE" "$JAR_NAME"
cd ../
zip "$ZIP_FILE" Procfile
rm Procfile

# ───── Step 4: Upload to S3 ───────────────────────────────
echo "Uploading $ZIP_FILE to S3..."
aws s3 cp "$ZIP_FILE" "s3://$BUCKET_NAME/$ZIP_FILE" \
  --region "$REGION" --profile "$PROFILE"

# ───── Step 5: Create new application version ─────────────
echo "Creating new Elastic Beanstalk application version..."
aws elasticbeanstalk create-application-version \
  --application-name "$APP_NAME" \
  --version-label "$ZIP_FILE" \
  --source-bundle S3Bucket="$BUCKET_NAME",S3Key="$ZIP_FILE" \
  --region "$REGION" \
  --profile "$PROFILE"

# ───── Step 6: Deploy new version to environment ──────────
echo "Deploying version $ZIP_FILE to environment $ENV_NAME..."
aws elasticbeanstalk update-environment \
  --environment-name "$ENV_NAME" \
  --version-label "$ZIP_FILE" \
  --region "$REGION" \
  --profile "$PROFILE"

echo "✅ Deployment complete."

rm -rf "$ZIP_FILE"