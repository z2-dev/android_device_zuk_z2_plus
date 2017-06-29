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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PocketModeService extends Service {
    private static final String TAG = "PocketModeService";
    private static final boolean DEBUG = false;

    private static final String CUST_INTENT = "com.cyanogenmod.settings.device.CUST_UPDATE";
    private static final String CUST_INTENT_EXTRA = "pocketmode_service";

    private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();

    private ProximitySensor mProximitySensor;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        mProximitySensor = new ProximitySensor(this);

        IntentFilter custFilter = new IntentFilter(CUST_INTENT);
        registerReceiver(mUpdateReceiver, custFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();
        if (receivers.contains(mScreenStateReceiver)) {
            this.unregisterReceiver(mScreenStateReceiver);
        }
        this.unregisterReceiver(mUpdateReceiver);
        mProximitySensor.disable();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onDisplayOn() {
        if (DEBUG) Log.d(TAG, "Display on");
        mProximitySensor.disable();
    }

    private void onDisplayOff() {
        if (DEBUG) Log.d(TAG, "Display off");
        mProximitySensor.enable();
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                onDisplayOn();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                onDisplayOff();
            }
        }
    };

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(CUST_INTENT_EXTRA, false)) {
                IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
                registerReceiver(mScreenStateReceiver, screenStateFilter);
                receivers.add(mScreenStateReceiver);
            } else if (receivers.contains(mScreenStateReceiver)) {
                unregisterReceiver(mScreenStateReceiver);
                receivers.remove(mScreenStateReceiver);
                mProximitySensor.disable();
            }
        }
    };
}
