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

# This contains the module build definitions for the hardware-specific
# components for this device.
#
# As much as possible, those components should be built unconditionally,
# with device-specific names to avoid collisions, to avoid device-specific
# bitrot and build breakages. Building a component unconditionally does
# *not* include it on all devices, so it is safe even with hardware-specific
# components.

LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_DEVICE),z2)

include device/zuk/z2/symlinks.mk

include $(call all-makefiles-under,$(LOCAL_PATH))

include $(CLEAR_VARS)

# END WiFi symlinks
$(shell mkdir -p $(TARGET_OUT_ETC)/firmware/wlan/qca_cld; \
    ln -sf /system/etc/wifi/WCNSS_qcom_cfg.ini \
	    $(TARGET_OUT_ETC)/firmware/wlan/qca_cld/WCNSS_qcom_cfg.ini; \
    ln -sf /persist/wlan_mac.bin \
	    $(TARGET_OUT_ETC)/firmware/wlan/qca_cld/wlan_mac.bin)

include $(call all-makefiles-under,$(LOCAL_PATH))

IMS_LIBS := libimscamera_jni.so libimsmedia_jni.so

IMS_SYMLINKS := $(addprefix $(TARGET_OUT)/app/ims/lib/arm64/,$(notdir $(IMS_LIBS)))
$(IMS_SYMLINKS): $(LOCAL_INSTALLED_MODULE)
	@echo "IMS lib link: $@"
	@mkdir -p $(dir $@)
	@rm -rf $@
	$(hide) ln -sf /system/vendor/lib64/$(notdir $@) $@

ALL_DEFAULT_INSTALLED_MODULES += $(IMS_SYMLINKS)

endif
