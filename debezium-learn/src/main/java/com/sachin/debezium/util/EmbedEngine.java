package com.sachin.debezium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author Sachin
 * @Date 2021/6/20
 **/
public class EmbedEngine  implements  Runnable{
    private final AtomicReference<Thread> runningThread = new AtomicReference<>();

    BlockingQueue task;


    private Logger LOGGER = LoggerFactory.getLogger(EmbedEngine.class);
    @Override
    public void run() {
        if (runningThread.compareAndSet(null, Thread.currentThread())) {
            while (runningThread.get() != null) {

                try {
                    LOGGER.debug("Embedded engine is polling task for records on thread {}", runningThread.get());
                     Object ob = task.poll(); // blocks until there are values ...
                    LOGGER.debug("Embedded engine returned from polling task for records");
                } catch (InterruptedException e) {
                    // Interrupted while polling ...
                    LOGGER.debug("Embedded engine interrupted on thread {} while polling the task for records", runningThread.get());
                    if (this.runningThread.get() == Thread.currentThread()) {
                        // this thread is still set as the running thread -> we were not interrupted
                        // due the stop() call -> probably someone else called the interrupt on us ->
                        // -> we should raise the interrupt flag
                        Thread.currentThread().interrupt();
                    }
                    break;
                }finally {
                    runningThread.set(null);
                }

            }


        }
        
    }

    public boolean isRunning() {
        return this.runningThread.get() != null;
    }

    public boolean stop() {
        LOGGER.info("Stopping the embedded engine");
        // Signal that the run() method should stop ...
        Thread thread = this.runningThread.getAndSet(null);
        if (thread != null) {
            try {
                // Making sure the event source coordinator has enough time to shut down before forcefully stopping it
                Duration timeout = Duration.ofMillis(Long
                        .valueOf(System.getProperty(WAIT_FOR_COMPLETION_BEFORE_INTERRUPT_PROP, Long.toString(WAIT_FOR_COMPLETION_BEFORE_INTERRUPT_DEFAULT.toMillis()))));
                LOGGER.info("Waiting for {} for connector to stop", timeout);
                latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            LOGGER.debug("Interrupting the embedded engine's thread {} (already interrupted: {})", thread, thread.isInterrupted());
            // Interrupt the thread in case it is blocked while polling the task for records ...
            thread.interrupt();
            return true;
        }
        return false;
    }
}
