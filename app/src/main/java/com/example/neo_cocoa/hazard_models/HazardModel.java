package com.example.neo_cocoa.hazard_models;

public class HazardModel {
    private static final int graphRange = 6; // 時間前までの接触人数の推移
    private static final int t1 =  1; // 人以上でレベル1
    private static final int t2 = 10; // 人以上でレベル2
    private static final int t3 = 20; // 人以上でレベル3
    private static final int t4 = 30; // 人以上でレベル4
    private static final int t5 = 40; // 人以上でレベル5


    private int locationData;
    private String address;
    private int last_contact;
    private int danger_level;

    public HazardModel () {
        // TODO: 位置情報権限の判定
    }

    public void loopTask () {
        // TODO: Loop task
    }

    private int get_danger_level (int num_of_contact) {
        if (num_of_contact < t1) {
            return 0;
        } else if (num_of_contact < t2) {
            return 1;
        } else if (num_of_contact < t3) {
            return 2;
        } else if (num_of_contact < t4) {
            return 3;
        } else if (num_of_contact < t5) {
            return 4;
        } else {
            return 5;
        }
    }


}
