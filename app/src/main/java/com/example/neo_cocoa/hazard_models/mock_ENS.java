package com.example.neo_cocoa.hazard_models;

import android.util.Log;

import java.util.Random;

/**
 * This is mock API of "Exposure Notification Service API".
 * This class provide random ENS Key values.
 * By using "get_num_at_key()" function, we can get number of people users contacted in each key's
 * expiration period.
 */
public class mock_ENS extends Thread {
    /** config **/
    private static final int refreshInterval = 10*1000; // change key2 value each <refreshInterval> ms.
    /************/
    private static boolean isRunning = false;
    private static long key2Value;
    private static long genTimeOfKey2;
    private static int contacted_person = 0;
    private static int last_contacted_person = 0;
    private static Random r = null;
    private static final String TAG = "mock_ENS";

    public mock_ENS() {
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
            simulate_exposure();
            try {
                Thread.sleep(1*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int get_num_at_key() {
        return contacted_person;
    }

    public long getKey2Value() {
        return key2Value;
    }

    public int get_num_at_prev_key() {
        return last_contacted_person;
    }

    private void refreshKey2() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - genTimeOfKey2 >= refreshInterval) {
            key2Value = r.nextInt();
            genTimeOfKey2 = nowTime;
            last_contacted_person = contacted_person;
            contacted_person = 0;
        } else {
            /* do nothing. */
        }
    }

    private void simulate_exposure() {
        if (r.nextInt() % 2 == 0) {
            Log.d(TAG, "exposure!");
            contacted_person++;
        }
    }
}
