package com.sachin.debezium.util;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @Author Sachin
 * @Date 2021/6/16
 * 这是一个latch，他的工作原理和{@link java.util.concurrent.CountDownLatch}类似，但是这个lath能够实现动态增加count
 **/
public class VariableLatch {


//    public  static VariableLatch create(){
//        return create(0);
//    }
//    public static VariableLatch create(int initialValue){
//        return new VariableLatch(initialValue);
//    }

    private static final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        /**
         * AQS共享模式acquireShared执行流程
         * 多个线程通过调用tryAcquireShared方法获取共享资源，返回值大于等于0则获取资源成功,返回值小于0则获取失败。
         * 当前线程获取共享资源失败后，通过调用addWaiter方法把该线程封装为Node节点，并设置该节点为共享模式。然后把该节点添加到队列的尾部。
         * 添加到尾部后,判断该节点的前驱节点是不是头节点，如果前驱节点是头节点，那么该节点的前驱节点出队列并获取共享资源,同时调用setHeadAndPropagate方法把该节点设置为新的头节点,同时唤醒队列中所有共享类型的节点,去获取共享资源。如果获取失败，则再次加入到队列中。
         * 如果该节点的前驱节点不是头节点,那么通过for循环进行自旋转等待,直到当前节点的前驱节点是头节点，结束自旋
         * <p>
         * 这个try方法会被父类的acquireShared方法调用。
         *
         * @param acquires
         * @return
         */

        @Override
        protected int tryAcquireShared(int acquires) {
            //为什么这个地方要判断为0？  int r = tryAcquireShared(arg);//这里也是获取state值，为0才会继续下去
            //CountDownLatch 中的实现也是通过getState判断是否为0
            return (getState() == 0) ? 1 : -1;
        }


        @Override
        protected boolean tryReleaseShared(int releases) {
            //递增或递减计数;信号转换到零时
            for (; ; ) {
                int c = getState();

                if (c == 0 && releases >= 0) return false;
                int nextc = c - releases;
                if (nextc < 0) nextc = 0;
                if (compareAndSetState(c, nextc)) {
                    return nextc == 0;
                }
            }
        }
    }

}
