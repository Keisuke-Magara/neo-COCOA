package com.example.neo_cocoa.hazard_models;

import java.sql.Time;
import java.util.Random;

/**
 * This is mock API of "Exposure Notification Service API".
 * This class provide random ENS Key values.
 * By using "get_num_at_key()" function, we get number of people users contacted in each key's
 * expiration period.
 */
public class mock_ENS {
    private int key2Value;
    private long genTimeOfKey2;
    private int num_of_person = 0;
    private Random r;
    private Time t;

    mock_ENS() {
        long stime = System.currentTimeMillis();
        this.r = new Random(stime);
        key2Value = r.nextInt();
        genTimeOfKey2 = stime;
    }

    public int get_num_at_key() {
        return num_of_person;
    }

    public void refreshKey2() {
        long nowTime = System.currentTimeMillis();
        if (nowTime - genTimeOfKey2 >= 20*60*1000) { // 20分以上前にキー生成
            key2Value = r.nextInt();
            genTimeOfKey2 = nowTime;
        } else {
            /* do nothing. */
        }
    }
}
