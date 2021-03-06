package com.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 16:15 2019/11/01
 */
public class MyFuture<V> implements RunnableFuture<V>{

    private final Callable<V> callable;

    private volatile V result;

    ReentrantLock lock = new ReentrantLock();

    Condition condition = lock.newCondition();
    boolean b =false;
    public MyFuture(Callable<V> callable) {
        this.callable = callable;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {

        return false;
    }

    @Override
   public boolean isDone(){
        return b;
    }

     @Override
    public V get() throws InterruptedException {

        lock.lock();
        try{
            if (result==null){
                condition.await();
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws  TimeoutException {
        final long deadline =  System.nanoTime() + unit.toNanos(timeout) ;
       while (result==null){

          long nanos = deadline - System.nanoTime();
          // System.out.println("get:"+result);
           if (nanos <= 0L) {

              throw  new TimeoutException();
           }
       }
      return result;
    }

    @Override
    public void run(){
        Callable<V> c = callable;
        if (c != null) {
            try {
                result=c.call();

                b=true;

                lock.lock();
                condition.signalAll();
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
                b = false;
            }


        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        MyFuture<String> myFuture = new MyFuture<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "my hello world!";
            }
        });
        executor.submit(myFuture);
     //   Thread.sleep(1000);
      System.out.println("开始执行");
  //  System.out.println(myFuture.get());
        System.out.println(myFuture.get(10000,TimeUnit.MILLISECONDS));

        FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(10000);
                return "hello world!";
            }
        });


        executor.submit(future);
        System.out.println("开始执行");
        System.out.println(future.get());
      System.out.println(future.get(1000,TimeUnit.MILLISECONDS));
        executor.shutdown();

    }
}
