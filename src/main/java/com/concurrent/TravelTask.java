package com.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 14:50 2019/10/29
 */
public class TravelTask implements Runnable{

    private MyCyclicBarrier cyclicBarrier;
    private String name;
    private int arriveTime;//赶到的时间

    public TravelTask(MyCyclicBarrier cyclicBarrier, String name, int arriveTime){
        this.cyclicBarrier = cyclicBarrier;
        this.name = name;
        this.arriveTime = arriveTime;
    }

    @Override
    public void run() {
        try {
            //模拟达到需要花的时间
            Thread.sleep(arriveTime * 1000);
            System.out.println(name +"到达集合点");
         //   try {
                cyclicBarrier.dowait();
//            } catch (BrokenBarrierException e) {
//                e.printStackTrace();
//            }
            System.out.println(name +"开始旅行啦～～");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}