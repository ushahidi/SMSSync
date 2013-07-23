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

function handle_bad_config {
	log "Please read BUILDING.txt for more info."
	log "BUILD FAILED"
	exit 1
}

log "Checking config..."
localPropsFile=local.properties
if [[ ! -f $localPropsFile ]]; then

	if [[ -z $ANDROID_HOME ]]; then
	    log "File not found: $localPropsFile"
    	log "environment variable ANDROID_HOME is not set."
    	handle_bad_config
	fi

fi

if ! grep -q '^sdk\.dir=' $localPropsFile; then
	log "value 'sdk.dir' not set in file '$localPropsFile'."
	handle_bad_config
fi


log "Config looks OK."

log "Building smssync..."
#./gradlew clean assemble
log "Smssync built."

log "Building test app..."
./gradlew clean connectedInstrumentTest --continue
log "Test app built."

log "BUILD COMPLETE"
