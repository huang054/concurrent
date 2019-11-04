package com.concurrent;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 16:15 2019/11/01
 */
public class MyFutureUnSafe<V> implements RunnableFuture<V>{

    private final Callable<V> callable;

    private  V result;
    private Object value;
    private static final Unsafe unsafe = getUnsafe();
    private static long valueOffset=0;
    static {
        try {
            valueOffset = unsafe.objectFieldOffset(Container.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    ReentrantLock lock = new ReentrantLock();

    Condition condition = lock.newCondition();
    boolean b =false;
    public MyFutureUnSafe(Callable<V> callable) {
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

           while(!unsafe.compareAndSwapInt(this, valueOffset, 1, 0)){

            }

        return result;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws  TimeoutException {
        final long deadline =  System.nanoTime() + unit.toNanos(timeout) ;

       while (unsafe.compareAndSwapInt(this, valueOffset, 1, 0)||result==null){

           long nanos = deadline - System.nanoTime();
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

                 V  result1=c.call();

                set(result1);
                b=true;

            } catch (Exception e) {
                e.printStackTrace();
                result = null;
                b = false;
            }
        }
    }
    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe)f.get(null);
        } catch (Exception e) {
        }
        return null;
    }
    protected void set(V v) {

        unsafe.putOrderedInt(this, valueOffset, 1);

            result = v;

    }
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        MyFutureUnSafe<String> myFuture = new MyFutureUnSafe<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "my hello world!";
            }
        });
        executor.submit(myFuture);
       System.out.println("开始执行");
   //System.out.println(myFuture.get());

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
