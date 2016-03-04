#!/usr/bin/env bash

echo "Starting instrumented tests"
./gradlew spoon -x lint --no-daemon -PdisablePreDex --stacktrace
rc=$? # Save spoon return value, to return it at the end of the script

# copy the test results to the test results directory.
cp -r app/build/spoon/* $CIRCLE_TEST_REPORTS/ # instrumented tests
adb logcat -d > $CIRCLE_ARTIFACTS/logcat_emulator.txt # logcat
exit $rc
