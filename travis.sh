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

#log "Building smssync..."
#gradle connectedInstrumentTest
#log "Smssync built."

#log "BUILD COMPLETE"

