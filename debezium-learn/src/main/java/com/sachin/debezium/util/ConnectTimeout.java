package com.sachin.debezium.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author Sachin
 * @Date 2021/6/19
 * 指定连接超时
 **/
public class ConnectTimeout {
    private ThreadFactory threadFactory;

    private void connect() {

        //scheduleDisconnectIn 内部 会启动一个线程计时，如果指定的时间内没有启动成功则关闭
        Callable callable = scheduleDisconnectIn(10000);

        try {
            //connect
        } catch (Exception e) {

        }finally {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void disConnect() {

    }

    private Callable scheduleDisconnectIn(final long timeOut) {
        final ConnectTimeout self = this;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Thread thread = newNamedThread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await(timeOut, TimeUnit.MILLISECONDS);
                } catch (InterruptedException interruptedException) {
                    //log
                }
                //连接成功之后会调用countDownLatch的countDown方法导致count=为0
                //如果count不为0则表示没有连接成功
                if (countDownLatch.getCount() != 0) {
                    //log  connect timeout
                    try {
                        self.disConnect();
                    } catch (Exception e) {
                        //log
                    }
                }

            }
        }, "threadName");
        thread.start();


        return new Callable() {
            @Override
            public Object call() throws Exception {
                countDownLatch.countDown();
                //阻塞调用call方法的线程，直到thread引用的线程执行结束。
                thread.join();
                return null;
            }
        };
    }

    private Thread newNamedThread(Runnable runnable, String threadName) {
        Thread thread = threadFactory == null ? new Thread(runnable) : threadFactory.newThread(runnable);
        thread.setName(threadName);
        return thread;
    }
}
