package com.sachin.debezium.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author Sachin
 * @Date 2021/6/19
 **/
public class ThreadUtils {

    public  ThreadUtils(){

    }
    public static ThreadFactory createThreadFactory(final String pattern,final boolean daemon){
        return new ThreadFactory() {
            private final AtomicLong threadEpoch = new AtomicLong();
            @Override
            public Thread newThread(Runnable r) {
                String threadName;
                if (pattern.contains("%d")) {
                    threadName = String.format(pattern, this.threadEpoch.addAndGet(1L));
                }else{
                    threadName = pattern;
                }
                Thread thread = new Thread(r, threadName);
                thread.setDaemon(daemon);
                return thread;
            }
        };
    }
}
