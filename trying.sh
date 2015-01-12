#!/bin/bash

# Make a release to the SMSSync app.
# TODO:: Look into making this a Gradle task

TMP_DIR='/tmp/website-src'

# cd into the website folder
echo "Compiling website"
pushd website-src
mkdir $TMP_DIR
ruhoh  compile $TMP_DIR
cp CNAME $TMP_DIR
popd

pushd $TMP_DIR
git init

git add .

git commit -m "Updating webiste..."

popd

echo "Done!"
