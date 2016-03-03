#!/usr/bin/env bash
echo "starting emulator"
mksdcard -l e 512M sdcard.img
emulator -avd circleci-android22 -no-audio -no-window -sdcard sdcard.img &
# Ensure that emulator booted
circle-android wait-for-boot
# sleep 30
echo "unlocking the emulator screen"
adb shell input keyevent 82
# Clean logcat
adb logcat -d

echo "Starting instrumented tests"
./gradlew spoon -x lint --no-daemon -PdisablePreDex --stacktrace


# copy the test results to the test results directory.
cp -r app/build/spoon/* $CIRCLE_TEST_REPORTS/$CIRCLE_NODE_INDEX # instrumented tests
adb logcat -d > $CIRCLE_ARTIFACTS/logcat_emulator$CIRCLE_NODE_INDEX.txt # logcat
