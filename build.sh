#!/bin/bash

##
# Build the test app and then the main app.
#

FAILED=0

# Build test app first
pushd ./smssync/tests

ant clean test

if [ "$?" = 1 ]; then
    echo "SMSSync test app build failed"
    FAILED=1
fi

# Build main app

cd ..

ant clean debug

if [ "$?" = 1 ]; then
    echo "SMSSync main app build failed"
fi

# Get back to where it all started
popd
exit $FAILED
