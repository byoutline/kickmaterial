#!/usr/bin/env bash
set -e
PROP=${ANDROID_HOME}/tools/source.properties
MIN_REV="Pkg.Revision=26.0.2"
CUR_REV=$(cat ${ANDROID_HOME}/tools/source.properties | grep "Revision=")

mkdir "$ANDROID_HOME/licenses" || true
echo -e "\n8933bad161af4178b1185d1a37fbf41ea5269c55" > "$ANDROID_HOME/licenses/android-sdk-license"

update_needed() {
    # Check whether current version is older than required.
    [  "${MIN_REV}" != "`echo -e "${MIN_REV}\n${CUR_REV}" | sort --version-sort | head -n1`" ]
}

# always update
for pack in "android-26" "platform-tools"  ;
    do echo y | android update sdk -u -a -t ${pack} ;
done ;

# update if needed
if update_needed ;
then cat ${PROP} && for pack in "tools" "extra-android-m2repository" "extra-google-m2repository" "extra-android-support" ;
    do echo y | android update sdk -u -a -t ${pack} ;
done ;
fi

if [ ! -e $ANDROID_HOME/build-tools/26.0.1 ]; then echo y | android update sdk -u -a -t "build-tools-26.0.1"; fi