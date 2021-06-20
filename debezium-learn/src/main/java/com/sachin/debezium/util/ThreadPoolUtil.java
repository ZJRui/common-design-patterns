package com.sachin.debezium.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Sachin
 * @Date 2021/6/19
 * 演示如何 停止一个线程池,参考实现 Debezium-parent中的MemoryOffsetBackingStore
 **/
public class ThreadPoolUtil {

    protected ExecutorService executorService;


    public void start() {
        executorService = new ThreadPoolExecutor(1, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), ThreadUtils.createThreadFactory(this.getClass().getSimpleName() + "%d", false), new ThreadPoolExecutor.AbortPolicy());

        //  executorService = Executors.newFixedThreadPool(1, ThreadUtils.createThreadFactory(this.getClass().getSimpleName() + "%d", false));
    }

    public void stop() {
        if (executorService != null) {
            executorService.shutdown();
            //尽最大努力等待任何 get() 和 set() 任务（和调用者的回调）完成。
            try {
                //阻塞直到所有任务在关闭请求后完成执行，或者超时发生，或者当前线程被中断，以先发生者为准。
                executorService.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();

            }
            if (!executorService.shutdownNow().isEmpty()) {
                throw new RuntimeException("Faild to stop .Exiting without cleanly shuting down pending tasks and/or callbacks");
            }
            executorService = null;

        }
    }
}

