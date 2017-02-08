/*
 * Copyright (C) 2017 The LineageOS Project
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

package com.cyanogenmod.settings.device;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

import cyanogenmod.providers.CMSettings;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();

    // Supported scancodes
    private static final int PRESS_SCANCODE = 102;
    private static final int TAP_SCANCODE = 158;
    private static final int LONG_TAP_SCANCODE = 183;
    private static final int SWIPE_RIGHT_SCANCODE = 249;
    private static final int SWIPE_LEFT_SCANCODE = 254;


    private final Context mContext;
    private Vibrator mVibrator;
    private boolean mPressDown;

    public KeyHandler(Context context) {
        mContext = context;
        mPressDown = false;

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mVibrator = null;
        }
    }

    public boolean handleKeyEvent(KeyEvent event) {
        int scanCode = event.getScanCode();

        switch (scanCode) {
            case PRESS_SCANCODE:
                if(event.getAction() == KeyEvent.ACTION_DOWN){
		  mPressDown=true;
		  android.util.Log.d(TAG, "handleKeyEvent: mPressDown set to true");
                }else{
		  mPressDown=false;
  		  android.util.Log.d(TAG, "handleKeyEvent: mPressDown set to false");
                }
                break;
            case TAP_SCANCODE:
                // nothing, just let it through
                break;
            case LONG_TAP_SCANCODE:
		if(mPressDown){
		  android.util.Log.d(TAG, "handleKeyEvent: mPressDown is true, LONG_TAP ignored");
		  return true;
                }
                break;
            case SWIPE_RIGHT_SCANCODE:
                doHapticFeedback();
                break;
            case SWIPE_LEFT_SCANCODE:
                doHapticFeedback();
                break;
            default:
                return false;
        }

        
        return false;

    }


    private void doHapticFeedback() {
        if (mVibrator == null) {
            return;
        }
        boolean enabled = CMSettings.System.getInt(mContext.getContentResolver(),
                CMSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, 1) != 0;
        if (enabled) {
            mVibrator.vibrate(20);
        }
    }
}
