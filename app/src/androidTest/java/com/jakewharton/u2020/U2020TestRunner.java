package com.jakewharton.u2020;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;

public final class U2020TestRunner extends AndroidJUnitRunner {
    private PowerManager.WakeLock wakeLock;

    // Permissions added in debug only manifest.
    @Override
    @SuppressLint("MissingPermission")
    public void onStart() {
        Context app = getTargetContext().getApplicationContext();

        String name = U2020TestRunner.class.getSimpleName();
        // Unlock the device so that the tests can input keystrokes.
        KeyguardManager keyguard = (KeyguardManager) app.getSystemService(KEYGUARD_SERVICE);
        //noinspection ResourceType
        keyguard.newKeyguardLock(name).disableKeyguard();
        // Wake up the screen.
        PowerManager power = (PowerManager) app.getSystemService(POWER_SERVICE);
        wakeLock = power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, name);
        wakeLock.acquire();

        super.onStart();
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}