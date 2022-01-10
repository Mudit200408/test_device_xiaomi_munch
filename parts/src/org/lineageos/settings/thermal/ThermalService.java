/*
 * Copyright (C) 2020 The LineageOS Project
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

package org.lineageos.settings.thermal;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.ActivityTaskManager.RootTaskInfo;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.app.Service;
import android.app.TaskStackListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import java.util.Arrays;

public class ThermalService extends Service {

    private static final String TAG = "ThermalService";

    private static final String SETTINGS_GAME_LIST = "gamespace_game_list";

    private String mPreviousApp;
    private ThermalUtils mThermalUtils;

    private IActivityTaskManager mActivityTaskManager;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPreviousApp = "";
            mThermalUtils.setDefaultThermalProfile();
            mThermalUtils.resetTouchModes();
        }
    };

    @Override
    public void onCreate() {
        dlog("Creating service");
        try {
            mActivityTaskManager = ActivityTaskManager.getService();
            mActivityTaskManager.registerTaskStackListener(mTaskListener);
        } catch (RemoteException e) {
            // Do nothing
        }
        mThermalUtils = new ThermalUtils(this);
        registerReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dlog("Starting service");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mThermalUtils.updateTouchRotation();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        this.registerReceiver(mIntentReceiver, filter);
    }

    private boolean isListedOnGameSpace(String packageName) {
        String[] gameList = Settings.System.getString(getContentResolver(),
                SETTINGS_GAME_LIST).split(";");
        if (packageName == null || gameList.length == 0) {
            return false;
        }

        return Arrays.stream(gameList).map(data -> {
            String[] userGame = data.split("=");
            return userGame.length == 2 ? userGame[0] : data;
        }).anyMatch(it -> it.equals(packageName));
    }

    private boolean isConfigured(String packageName) {
        return mThermalUtils.getStateForPackage(packageName) != ThermalUtils.STATE_DEFAULT;
    }

    private final TaskStackListener mTaskListener = new TaskStackListener() {
        @Override
        public void onTaskStackChanged() {
            try {
                final RootTaskInfo info = mActivityTaskManager.getFocusedRootTaskInfo();
                if (info == null || info.topActivity == null) {
                    return;
                }

                String foregroundApp = info.topActivity.getPackageName();
                if (!foregroundApp.equals(mPreviousApp)) {
                    if (!isConfigured(foregroundApp) && isListedOnGameSpace(foregroundApp)) {
                        mThermalUtils.setThermalProfileForce(ThermalUtils.STATE_GAMING);
                    } else {
                        mThermalUtils.setThermalProfile(foregroundApp);
                    }
                    mPreviousApp = foregroundApp;
                }
            } catch (Exception e) {}
        }
    };
    private static void dlog(String msg) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, msg);
        }
    }
}
