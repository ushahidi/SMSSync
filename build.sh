#!/bin/bash
set -e

noSdkUpdate=false
while [[ $1 == "--"* ]]; do
	if [[ $1 == "--no-sdk-update" ]]; then
		noSdkUpdate=true
	fi
	shift
done

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
if [[ ! -f local.properties ]]; then
	log "File not found: local.properties"
	handle_bad_config
fi

if ! grep -q '^sdk\.dir=' local.properties; then
	log "value 'sdk.dir' not set in file 'local.properties'."
	handle_bad_config
fi

if [[ -z $ANDROID_HOME ]]; then
	log "environment variable ANDROID_HOME is not set."
	handle_bad_config
fi
log "Config looks OK."

if ! $noSdkUpdate; then
	read -p "Do you want to update android SDK?  (This may be necessary for the build to run.) [y/N] " -n 1 -r
	if [[ $REPLY =~ ^[Yy]$ ]]; then
		log "Updating android SDK..."
		android update sdk --no-ui
		log "Android SDK updated."
	else
		log "Skipping android SDK update."
	fi
fi

log "Building test app..."
cd ./smssync/tests
ant clean build-project
log "Test app built."

log "Building smssync..."
cd ..
ant clean debug
log "Smssync built."

log "BUILD COMPLETE"

