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

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final boolean DEBUG = true;


    // Supported scancodes
    private static final int PRESS_SCANCODE = 102;
    private static final int TAP_SCANCODE = 158;
    private static final int LONG_TAP_SCANCODE = 183;
    private static final int SWIPE_RIGHT_SCANCODE = 249;
    private static final int SWIPE_LEFT_SCANCODE = 254;

    /**
     * How much we should wait before allowing short taps after home is pressed
     */
    private static final long PRESS_DELAY_THRESHOLD_MS = 400;


    private final Context mContext;
    private Handler mHandler;
    private boolean mPressDown;
    private boolean mRecentPress;

    private final Runnable mUnlockRunnable;

    public KeyHandler(Context context) {
        mContext = context;
        mPressDown = false;
        mHandler = new Handler();
        mUnlockRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (DEBUG) {
                      Log.d(TAG,"sleeping for " + PRESS_DELAY_THRESHOLD_MS + " ms");
                    }
                    Thread.sleep(PRESS_DELAY_THRESHOLD_MS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: ", e);
                } finally {
                  releasePressDelay();
                }
            }
        };
    }

    public boolean handleKeyEvent(KeyEvent event) {
        int scanCode = event.getScanCode();

        switch (scanCode) {
            case PRESS_SCANCODE:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mPressDown = true;
                    setPressDelay();
                    new Thread(mUnlockRunnable).start();
                    Log.d(TAG, "handleKeyEvent: mPressDown set to true");
                } else {
                    mPressDown = false;
                    Log.d(TAG, "handleKeyEvent: mPressDown set to false");
                }
                break;
            case TAP_SCANCODE:
                if (readPressDelay()) {
                    // home has just been pressed, so short tap is a fluke. deny!
                    if (DEBUG) {
                        Log.d(TAG, "handleKeyEvent: mRecentPress is true, ignore short tap");
                    }
                    return true;
                }
                break;
            case LONG_TAP_SCANCODE:
                if (mPressDown) {
                    Log.d(TAG, "handleKeyEvent: mPressDown is true, LONG_TAP ignored");
                    return true;
                }
                break;
            case SWIPE_RIGHT_SCANCODE:
                // nothing, just let it through
                break;
            case SWIPE_LEFT_SCANCODE:
                // nothing, just let it through
                break;
            default:
                return false;
        }


        return false;

    }

    private synchronized void setPressDelay() {
        if (DEBUG) {
            Log.d(TAG, "setPressDelay() called");
        }
        this.mRecentPress = true;
    }

    private synchronized void releasePressDelay() {
        if (DEBUG) {
            Log.d(TAG, "releasePressDelay() called");
        }
        this.mRecentPress = false;
    }

    private synchronized boolean readPressDelay() {
        if (DEBUG){
            Log.d(TAG, "readPressDelay called, returning " + this.mRecentPress);
        }
        return this.mRecentPress;
    }

}
