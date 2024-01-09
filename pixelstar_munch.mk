#
# Copyright (C) 2021 The LineageOS Project
#
# SPDX-License-Identifier: Apache-2.0
#

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit_only.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)

# Inherit some common lineage stuff.
$(call inherit-product, vendor/pixelstar/config/common_full_phone.mk)

# Inherit from munch device
$(call inherit-product, device/xiaomi/munch/device.mk)

PRODUCT_NAME := pixelstar_munch
PRODUCT_DEVICE := munch
PRODUCT_MANUFACTURER := Xiaomi

PRODUCT_GMS_CLIENTID_BASE := android-xiaomi

# Bootanimation Resolution
TARGET_BOOT_ANIMATION_RES := 1080

WITH_GAPPS := true
# Inherit some common device props
TARGET_SUPPORTS_QUICK_TAP := true
TARGET_FACE_UNLOCK_SUPPORTED := true
TARGET_SUPPORTS_CALL_RECORDING := true
TARGET_SUPPORTS_GOOGLE_RECORDER := true

# Google Assistant
TARGET_SUPPORTS_NEXT_GEN_ASSISTANT := true

#Pixel Charger
USE_PIXEL_CHARGER := true

