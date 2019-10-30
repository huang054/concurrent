package com.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 14:08 2019/10/30
 */
public class MyLinkedBlockingQueue<E> {

    static final class Node<E> {

        E item;

        Node<E> next;

        Node(E x) {
            item = x;
        }
    }
    private final int capacity;


    private final AtomicInteger count = new AtomicInteger();


    transient Node<E> head;


    private transient Node<E> last;


    private final ReentrantLock takeLock = new ReentrantLock();

    private final Condition notEmpty = takeLock.newCondition();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notFull = putLock.newCondition();
    public MyLinkedBlockingQueue(int capacity) {
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }
    public MyLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    //从尾部加入node节点
    private void enqueue(Node<E> node) {

        last = last.next = node;
    }
    //头结点的item始终为null，其实是从第二个节点取
    private E dequeue() {
       Node<E> h=head;
       Node<E> first=head.next;
       h.next=h;
       E item=first.item;
       first.item=null;
       head=first;
       return item;
    }
    public void put(E e) throws InterruptedException {
           if (e==null){
               throw new NullPointerException();
           }
        int c=-1;
        Node<E> node = new Node<E>(e);
        ReentrantLock put=putLock;

        put.lockInterruptibly();
         try{

             while (count.get()==capacity){
                 notFull.await();
             }
             enqueue(node);
             c=count.getAndIncrement();
             if (c+1<capacity){
                 notFull.signal();
             }

         }finally {
             put.unlock();
         }
         if (c==0){
             ReentrantLock lock1=takeLock;
             lock1.lock();
             try{
                 notEmpty.signal();
             }finally {
                 lock1.unlock();
             }
         }
    }

    public E take() throws InterruptedException {
             E item;
             ReentrantLock lock = takeLock;

             int c=-1;
             lock.lockInterruptibly();
           AtomicInteger atomicInteger=count;
             try{
                 while (atomicInteger.get()==0){
                     notEmpty.await();
                 }
                 item=dequeue();
                 c=atomicInteger.getAndDecrement();
                 if (c>1){
                     notEmpty.signal();
                 }
             }finally {
                 lock.unlock();
             }
             if (c==capacity){
                 ReentrantLock lock1=putLock;
                 lock1.lock();
                 try {
                     notFull.signal();
                 }finally {
                     lock1.unlock();
                 }
             }
             return item;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println((16 - 1) >>> 1);
        MyLinkedBlockingQueue<String> myLinkedBlockingQueue = new MyLinkedBlockingQueue<>(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myLinkedBlockingQueue.put("hello1");
                    myLinkedBlockingQueue.put("hello2");
                    myLinkedBlockingQueue.put("hello3");
                    myLinkedBlockingQueue.put("hello4");
                    myLinkedBlockingQueue.put("hello5");
                    myLinkedBlockingQueue.put("hello6");
                    myLinkedBlockingQueue.put("hello7");
                    myLinkedBlockingQueue.put("hello8");
                    myLinkedBlockingQueue.put("hello9");
                    myLinkedBlockingQueue.put("hello10");
                    myLinkedBlockingQueue.put("hello11");
                    myLinkedBlockingQueue.put("hello12");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        Thread.sleep(5000);
        System.out.println(myLinkedBlockingQueue.count.get());
        new Thread(()->{

                while(true){
                    try {
                    System.out.println(myLinkedBlockingQueue.take());
                    Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


        }).start();


    }
}
