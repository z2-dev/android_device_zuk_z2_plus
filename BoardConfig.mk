#
# Copyright (C) 2016 The CyanogenMod Project
# Copyright (C) 2017 The LineageOS Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Inherit from common msm8996-common
-include device/zuk/msm8996-common/BoardConfigCommon.mk

AUDIO_FEATURE_ENABLED_SPKR_PROTECTION := true

DEVICE_PATH := device/zuk/z2_plus

# Assert
TARGET_OTA_ASSERT_DEVICE := z2_plus

# Kernel
TARGET_KERNEL_CONFIG := z2_plus_defconfig

# Lights (see liblight/Android.mk in msm8996-common)
TARGET_LIGHTS_ONLY_RED_LED := true

# Properties
TARGET_SYSTEM_PROP += $(LOCAL_PATH)/system.prop

# Inherit from the proprietary version
-include vendor/zuk/z2_plus/BoardConfigVendor.mk
