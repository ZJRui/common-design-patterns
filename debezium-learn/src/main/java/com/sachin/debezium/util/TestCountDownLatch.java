package com.sachin.debezium.util;

import java.util.concurrent.CountDownLatch;

/**
 * @Author Sachin
 * @Date 2021/6/17
 **/
public class TestCountDownLatch {
    public static final CountDownLatch countDownLatch = new CountDownLatch(2);
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
