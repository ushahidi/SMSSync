#!/bin/bash

# Make a release to the SMSSync app.
# TODO:: Look into making this a Gradle task

if [[ $# -lt 2 ]]
then
  echo "Usage: release.sh [version number. eg.v2.0.1] [tag message eg. Bug release]"
  exit 1
fi

TAG_NAME=$1
TAG_MESSAGE=$2
DEVELOP='develop'
MASTER='master'
echo $TAG_MESSAGE

# SMSSync source code
SMSSYNC='smssync'

# Merge develop branch into master

# Checkout master branch
echo "Checking out the master branch..."
git checkout $MASTER

# Merge develop branch into master branch
echo "Merging develop branch into master"
git merge $DEVELOP

# Create the signed release tag
echo "Creating release tag $TAG_NAME ..."
git tag -s $TAG_NAME -m $TAG_MESSAGE

# Create a release apk
echo "Building a release apk"
./gradlew clean assemble

# Back to where we were before
popd

# Checkout develop branch
echo "Checking out develop branch..."
git checkout $DEVELOP

echo "Done!"
