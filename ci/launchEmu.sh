#!/usr/bin/env bash
if [ "$1" = "0" ] ; then EMU=circleci-android22 ; else EMU=testing18 ; fi
echo "launching $EMU"
emulator -avd ${EMU} -no-audio -no-window -no-boot-anim -sdcard mysdcard.img