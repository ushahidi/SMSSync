#!/bin/bash

# Make a release to the SMSSync app.

if [[ $# -lt 2 ]]
then
  echo "Usage: release.sh [version number. eg.v2.0.1] [tag message eg. Bug release]"
  exit 1
fi

TAG_NAME=$1
TAG_MESSAGE=$2
DEVELOP='develop'
MASTER='master'
RELEASE='release'
echo $TAG_MESSAGE

# SMSSync source code
SMSSYNC='smssync'

# Merge release to master and develop

# Checkout develop branch
echo "Checking out the develop branch..."
git checkout $DEVELOP

# Merge release branch into the develop branch
echo "Merging release branch into develop..."
git merge $RELEASE

# Checkout master branch
echo "Checking out the master branch..."
git checkout $MASTER

# Merge release branch into master branch
echo "Merging release branch into"
git merge $RELEASE

# Delete the release branch
echo "Deleting local release branch..."
git branch -d $RELEASE

# Delete remote release branch
echo "Deleting remote release branch..."
git push origin --delete $RELEASE 

# Create the release tag
echo "Creating release tag $TAG_NAME ..."
git tag -a $TAG_NAME -m $TAG_MESSAGE

# Push newly created tag to remote host
echo "Pushing new tag to remote host..."
git push --tags

# Create the release build
echo "Changing directory into $SMSSYNC"
pushd $SMSSYNC

# Create a release apk
echo "Building a release apk"
ant clean release

# Back to where we were before
popd

# Checkout develop branch
echo "Checking out develop branch..."
git checkout $DEVELOP

echo "Done!"
