#!/bin/bash

if [[ $# -lt 3 ]]
then
  echo "Usage: release.sh [Scope; valid are major, minor, patch] [Stage; valid are alpha, beta, rc, final, dev] [Track; valid are alpha, production, beta]"
  echo "Eg. command ./release.sh minor alpha alpha"
  exit
fi

SCOPE=$1
STAGE=$2
TRACK=$3

echo "Releasing..."

TAG_NAME=$1
TAG_MESSAGE=$2
MASTER='master'
echo $TAG_MESSAGE

# SMSSync source code
SMSSYNC='smssync'

# Checkout master branch
echo "Checking out the master branch..."
git checkout $MASTER

# Create a release apk
echo "Building a release apk"
./gradlew clean

./gradlew release -Prelease.scope=$SCOPE -Prelease.stage=$STAGE -PuploadTrack=$TRACK

echo "Done!"
