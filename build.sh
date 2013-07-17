#!/bin/bash
set -e

function log {
	local logStart="#"
	local sourceLen=${#BASH_SOURCE[@]}
	for ((i=$sourceLen-1; i>0; --i)); do
		logStart="$logStart [$(basename ${BASH_SOURCE[$i]})]"
	done
	echo "$logStart $@"
}

# Confirm that local.build.properties file exists
if [[ ! -f local.properties ]]; then
	log "File not found: local.properties"
	log "Please read BUILDING.txt for more info."
	log "BUILD FAILED"
	exit 1
fi

##
# Build the test app and then the main app.
#

# Build test app first
pushd ./smssync/tests
ant clean build-project

cd ..
ant clean debug

# Get back to where it all started
popd
log "BUILD COMPLETE"

