#!/bin/bash
#
# Copyright (C) 2013-2016, The CyanogenMod Project
# Copyright (C) 2017, The LineageOS Project
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

# Required!
DEVICE=z2_plus
VENDOR=zuk

# Load extractutils and do some sanity checks
MY_DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$MY_DIR" ]]; then MY_DIR="$PWD"; fi

CM_ROOT="$MY_DIR"/../../..

# Qualcomm BSP blobs - we put a conditional around here
# in case the BSP is actually being built
printf '\n%s\n' "ifeq (\$(QCPATH),)" >> "$PRODUCTMK"
printf '\n%s\n' "ifeq (\$(QCPATH),)" >> "$ANDROIDMK"


# Qualcomm performance blobs - conditional as well
# in order to support LineageOS builds
cat << EOF >> "$PRODUCTMK"
endif

-include vendor/extra/devices.mk
ifneq (\$(call is-qc-perf-target),true)
EOF

cat << EOF >> "$ANDROIDMK"
endif

ifneq (\$(TARGET_HAVE_QC_PERF),true)
EOF



echo "endif" >> "$PRODUCTMK"

cat << EOF >> "$ANDROIDMK"

endif

EOF

printf '\n%s\n' "\$(call inherit-product, vendor/qcom/binaries/msm8996/graphics/graphics-vendor.mk)" >> "$PRODUCTMK"

# We are done!
write_footers

