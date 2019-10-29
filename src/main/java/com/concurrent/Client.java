package com.concurrent;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 14:52 2019/10/29
 */
public class Client {

    public static void main(String[] args) throws Exception{

        MyCyclicBarrier cyclicBarrier = new MyCyclicBarrier(3,new TourGuideTask());
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //登哥最大牌，到的最晚
        executor.execute(new TravelTask(cyclicBarrier,"哈登",5));
        executor.execute(new TravelTask(cyclicBarrier,"保罗",3));
        executor.execute(new TravelTask(cyclicBarrier,"戈登",1));
        executor.shutdown();
    }
}