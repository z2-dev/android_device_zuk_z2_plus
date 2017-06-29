/*
 * Copyright (c) 2016 The CyanogenMod Project
 *               2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.pocketmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

public class Startup extends BroadcastReceiver {

    private static final String TAG = "ZukPocketMode";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (cyanogenmod.content.Intent.ACTION_INITIALIZE_CM_HARDWARE.equals(action)) {
            Log.d(TAG, "Starting");
            context.startServiceAsUser(new Intent(context, PocketModeService.class),
                    UserHandle.CURRENT);
        }
    }
}
