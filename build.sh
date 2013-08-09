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

function no_device_running_create_one {
    if ! adb devices | grep -w -q 'device'; then
        create_emulator_with_no_gui
    fi
}

function create_emulator_with_no_gui {
    log "Creating emulator..."
    echo yes | android update sdk --filter sysimg-17 --no-ui --force > /dev/null

    echo no | android create avd --force -n test -t android-17 --abi armeabi-v7a
    emulator -avd test -no-skin -no-audio -no-window &

    chmod +x ci/wait_for_emulator.sh
    ci/wait_for_emulator.sh
    adb shell input keyevent 82

    log "Complete!"
}

function kill_running_emulator {
    log "Killing emulator..."
    adb -s emulator-5554 emu kill
    log "Done!"
}

log "Checking config..."
localPropsFile=local.properties
if [[ -z $ANDROID_HOME ]]; then
    log "environment variable ANDROID_HOME is not set."

    if [[ ! -f $localPropsFile ]]; then
        log "File not found: $localPropsFile"
        handle_bad_config
    fi

    if ! grep -q '^sdk\.dir=' $localPropsFile; then
    	log "value 'sdk.dir' not set in file '$localPropsFile'."
    	handle_bad_config
    fi
fi


log "Config looks OK."

log "Building smssync..."
./gradlew clean assemble
log "Smssync built."

# test build requires a running emulator. Create and run and emulator
log "About to build test app"
no_device_running_create_one

log "Building test app..."
./gradlew clean connectedInstrumentTest --continue
log "Test app built."

log "BUILD COMPLETE"
exit 0

