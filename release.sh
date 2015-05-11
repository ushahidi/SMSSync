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
DEVELOP='develop'
MASTER='master'
TMP_DIR='/tmp/public'
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

# Create a release apk
echo "Building a release apk"
./gradlew clean

./gradlew release -Prelease.scope=$SCOPE -Prelease.stage=$STAGE -PuploadTrack=$TRACK

# Checkout develop branch
echo "Checking out develop branch..."
git checkout $DEVELOP

echo "Done!"
