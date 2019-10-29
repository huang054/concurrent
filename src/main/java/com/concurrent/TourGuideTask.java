package com.concurrent;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 14:52 2019/10/29
 */
public class TourGuideTask implements Runnable{

    @Override
    public void run() {
        System.out.println("****导游分发护照签证****");
        try {
            //模拟发护照签证需要2秒
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}