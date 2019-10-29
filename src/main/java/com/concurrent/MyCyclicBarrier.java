package com.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 11:29 2019/10/29
 */
public class MyCyclicBarrier {

    public static class Generation{

    }
    private final  Lock lock = new ReentrantLock();
    Condition trip = lock.newCondition();
    private Generation generation = new Generation();
    private int count;
    private final int parties;
    private final Runnable barrierCommand;
    public MyCyclicBarrier(int count,Runnable runnable){
        this.count=count;
        this.parties=count;
        barrierCommand=runnable;
    }

    private void nextGeneration() {
        // signal completion of last generation
        trip.signalAll();
        // set up next generation
        count = parties;
        generation = new Generation();
    }

    public int dowait(){
      final Lock lock =this.lock;
        lock.lock();
        try {
            int index = --count;
            if (index == 0) {
                barrierCommand.run();
                nextGeneration();
                return 0;
            }

                try {
                    trip.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0;
        }finally {
            lock.unlock();
        }

    }

    public static void main(String[] args) {

    }
}
