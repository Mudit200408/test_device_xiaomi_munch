/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

#include <libinit_dalvik_heap.h>
#include <libinit_variant.h>

#include "vendor_init.h"

static const variant_info_t munch_in_info = {
    .hwc_value = "INDIA",
    .name = "munch_global",
    .brand = "POCO",
    .device = "munch",
    .marketname = "POCO F4",
    .mod_device = "munch_in_global",
    .model = "22021211RI",
};

static const variant_info_t munch_global_info = {
    .hwc_value = "GLOBAL",
    .name = "munch_global",
    .brand = "POCO",
    .device = "munch",
    .marketname = "POCO F4",
    .mod_device = "munch_global",
    .model = "22021211RG",
};

static const variant_info_t munch_info = {
    .hwc_value = "CN",
    .name = "munch_global",
    .brand = "Redmi",
    .device = "munch",
    .marketname = "Redmi K40S",
    .mod_device = "munch_global",
    .model = "22021211RC",
};


static const std::vector<variant_info_t> variants = {
    munch_in_info,
    munch_global_info,
    munch_info,
};

void vendor_load_properties() {
    search_variant(variants);
    set_dalvik_heap();
}
