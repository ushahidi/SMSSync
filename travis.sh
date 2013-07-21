#!/bin/bash
set -e

# Use by travic CI --https://travis-ci.org/ushahidi/SMSSync to build SMSSync 

function log {
	local logStart="#"
	local sourceLen=${#BASH_SOURCE[@]}
	for ((i=$sourceLen-1; i>0; --i)); do
		logStart="$logStart [$(basename ${BASH_SOURCE[$i]})]"
	done
	echo "$logStart $@"
}

log "Building smssync..."
pushd smssync
gradle connectedInstrumentTest
popd
log "Smssync built."

log "BUILD COMPLETE"

# This uses ant. Gradually migrating to gradle. Disabling ant for now 
#log "Building smssync..."
#pushd smssync
#ant clean debug
#popd
#log "Smssync built."

#log "Building test app..."
#pushd smssync/tests
#ant clean build-project
#popd
#log "Test app built."

#log "BUILD COMPLETE"

