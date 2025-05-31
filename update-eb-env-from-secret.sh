#!/bin/bash

# ========== CONFIG ==========
SECRET_NAME="nfe-reader-processor"
PROFILE="aws-arnaldo"     # Change if needed
REGION="us-east-1"             # Change if needed
ENV_NAME="my-java-env"       # Your EB environment
APP_NAME="my-java-app"       # Your EB application name
TMP_JSON="env-vars.json"
# =============================

echo "Fetching secret $SECRET_NAME from Secrets Manager..."
SECRET_STRING=$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_NAME" \
  --query SecretString \
  --output text \
  --region "$REGION" \
  --profile "$PROFILE")

if [ $? -ne 0 ]; then
  echo "❌ Failed to fetch secret."
  exit 1
fi

echo "Parsing secret into Elastic Beanstalk option-settings format..."

# Convert secret JSON to EB format
echo "$SECRET_STRING" | jq -r 'to_entries | map({
  Namespace: "aws:elasticbeanstalk:application:environment",
  OptionName: .key,
  Value: .value
})' > "$TMP_JSON"

if [ ! -s "$TMP_JSON" ]; then
  echo "❌ Failed to convert secret to EB format."
  exit 1
fi

echo "Updating Elastic Beanstalk environment variables for $ENV_NAME..."
aws elasticbeanstalk update-environment \
  --application-name "$APP_NAME" \
  --environment-name "$ENV_NAME" \
  --option-settings file://"$TMP_JSON" \
  --region "$REGION" \
  --profile "$PROFILE"

if [ $? -eq 0 ]; then
  echo "✅ Environment variables updated successfully."
else
  echo "❌ Failed to update environment variables."
fi

# Clean up
rm "$TMP_JSON"