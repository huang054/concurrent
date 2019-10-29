package com.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 10:08 2019/10/29
 */
public class CountdownLatch {

    int count;

    Lock lock = new ReentrantLock();

    Condition condition = lock.newCondition();

    public CountdownLatch(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }
      this.count=count;
    }

    public void await() throws InterruptedException {
        lock.lock();
        if (Thread.interrupted()){
            throw new InterruptedException();
        }
        try {
            while (count>0){
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public int getCount(){
        return count;
    }

    public void countDown() {
          lock.lock();
          count--;
         if(count<1){
            condition.signalAll();
        }
             lock.unlock();
         }

    private static void action() {
        System.out.printf("线程[%s] 正在执行...\n", Thread.currentThread().getName());  // 2
    }
    public static void main(String[] args) throws InterruptedException {
        CountdownLatch latch = new CountdownLatch(5);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                action();
                latch.countDown(); // -1
            });
        }

        // 等待完成
        // 当计数 > 0，会被阻塞
        latch.await();

        System.out.println("Done");

        // 关闭线程池
        executorService.shutdown();
    }
}


