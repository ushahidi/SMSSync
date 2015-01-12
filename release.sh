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
TMP_DIR='/tmp/website-src'
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
git tag $TAG_NAME -m $TAG_MESSAGE

# Create a release apk
echo "Building a release apk"
./gradlew clean assemble

# Push tags to remote repo
echo "Pushing tags to remote repo..."
git push ushahidi master develop --tags && git push origin master develop --tags

# Compile HTML files

# cd into the website folder
echo "Compiling website"

pushd website-src

bundle exec ruhoh compile $TMP_DIR

cp CNAME $TMP_DIR
popd

pushd $TMP_DIR
git init

git add .

git commit -m "Updating webiste..."

git push git@github.com:ushahidi/SMSSync.git master:gh-pages --force

popd

echo "Website update done"


# Checkout develop branch
echo "Checking out develop branch..."
git checkout $DEVELOP

echo "Done!"
