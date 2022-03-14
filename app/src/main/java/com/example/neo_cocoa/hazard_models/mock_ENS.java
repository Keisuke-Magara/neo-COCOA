package com.example.neo_cocoa.hazard_models;

import android.util.Log;

import java.sql.Time;
import java.util.Random;

/**
 * This is mock API of "Exposure Notification Service API".
 * This class provide random ENS Key values.
 * By using "get_num_at_key()" function, we can get number of people users contacted in each key's
 * expiration period.
 */
public class mock_ENS extends Thread {
    private static boolean isRunning = false;
    private int key2Value;
    private long genTimeOfKey2;
    private int contacted_person = 0;
    private Random r = null;
    private Time t;
    private static final String TAG = "mock_ENS";

    mock_ENS() {
        super();
        if (!isRunning) {
            isRunning = true;
            long sysTime = System.currentTimeMillis();
            r = new Random(sysTime);
            key2Value = r.nextInt();
            genTimeOfKey2 = sysTime;
        }else{
            Log.println(Log.ASSERT, TAG, "multiple instances are exists!");
            System.exit(1);
        }
    }

    /**
     * Loop task
     */
    public void run() {
        Log.d(TAG, "Loop task started.");
        while(true) {
            refreshKey2();
            exposure();
        }
    }

    public int get_num_at_key() {
        return contacted_person;
    }

    public int getKey2Value() {
        return key2Value;
    }

    private void refreshKey2() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - genTimeOfKey2 >= 20*60*1000) { // 20分以上前にキー生成
            key2Value = r.nextInt();
            genTimeOfKey2 = nowTime;
            contacted_person = 0;
        } else {
            /* do nothing. */
        }
    }

    private void exposure() {
        if (r.nextInt() % 3571 == 0) {
            contacted_person++;
        }
    }
}
