#!/bin/bash
#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

# Load extractutils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$MY_DIR" ]]; then MY_DIR="$PWD"; fi

CM_ROOT="$MY_DIR"/../../..

HELPER="$CM_ROOT"/vendor/cm/build/tools/extract_utils.sh
if [ ! -f "$HELPER" ]; then
    echo "Unable to find helper script at $HELPER"
    exit 1
fi
. "$HELPER"

while getopts ":nhsd:" options
do
  case $options in
    n ) CLEANUP="false" ;;
    d ) SRC=$OPTARG ;;
    s ) SETUP=1 ;;
    h ) echo "Usage: `basename $0` [OPTIONS] "
        echo "  -n  No cleanup"
        echo "  -d  Fetch blob from filesystem"
        echo "  -s  Setup only, no extraction"
        echo "  -h  Show this help"
        exit ;;
    * ) ;;
  esac
done

if [ -z $SRC ]; then
  SRC=adb
fi

if [ -n "$SETUP" ]; then
    # Initialize the helper for common
    setup_vendor "$DEVICE_COMMON" "$VENDOR" "$CM_ROOT" true false
    "$MY_DIR"/setup-makefiles.sh false

    if [ -s "$MY_DIR"/../$DEVICE/proprietary-files.txt ]; then
        # Initalize the helper for device
        setup_vendor "$DEVICE" "$VENDOR" "$CM_ROOT" false false
        "$MY_DIR"/setup-makefiles.sh false
    fi
else
    # Initialize the helper for common
    setup_vendor "$DEVICE_COMMON" "$VENDOR" "$CM_ROOT" true "$CLEANUP"

    extract "$MY_DIR"/proprietary-files.txt "$SRC"

    if [ -s "$MY_DIR"/../$DEVICE/proprietary-files.txt ]; then
        # Reinitialize the helper for device
        setup_vendor "$DEVICE" "$VENDOR" "$CM_ROOT" false "$CLEANUP"

        extract "$MY_DIR"/../$DEVICE/proprietary-files.txt "$SRC"
    fi

    "$MY_DIR"/setup-makefiles.sh "$CLEANUP"
fi
