package com.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 11:07 2019/10/30
 */
public class MyLinkedBlockingDeque<E> {
    public MyLinkedBlockingDeque(int capacity) {
        this.capacity = capacity;
    }
    public MyLinkedBlockingDeque() {
       this(Integer.MAX_VALUE);
    }

    static final class Node<E> {

        E item;


        Node<E> prev;


        Node<E> next;

        Node(E x) {
            item = x;
        }
    }
    transient Node<E> first;


    transient Node<E> last;


    private transient int count;


    private final int capacity;


    final ReentrantLock lock = new ReentrantLock();


    private final Condition notEmpty = lock.newCondition();


    private final Condition notFull = lock.newCondition();
    private boolean linkFirst(Node<E> node) {
        if (count>capacity) {
            return false;
        }
        Node<E> node1=first;
        node.next=first;
        first=node;
        if (last==null) {
            last = node;
        }else {
            node1.prev = node;
        }
        ++count;
        notEmpty.signal();
            return true;
    }
    private boolean linkLast(Node<E> node) {
        if (count>capacity) {
            return false;
        }
        Node<E> node1=last;
        node.prev=last;
        last=node;
        if (first==null){
            first=node;
        }else{
            node1.next=node;
        }
        ++count;
        notEmpty.signal();
        return true;
    }
    private E unlinkFirst() {
        Node<E> node=first;
        if (first==null){
            return null;
        }
        Node node1=node.next;
        E item =node.item;
        node.item=null;
        node.next=node;
        first=node1;
        if (node1==null){
            last=null;
        }else{
            node1.prev=null;
        }
        --count;
        notFull.signalAll();
        return item;
    }
    private E unlinkLast() {
        Node<E> node=last;
        if (last==null){
            return null;
        }
        Node node1=last.prev;
        E item =node.item;
        node.item=null;
        node.prev=node;
        last=node1;
        if (node1==null){
            first=null;
        }else{
            node1.next=null;
        }
        --count;
        notFull.signalAll();
        return item;
    }
    public boolean offerFirst(E e) {
        if (e == null) throw new NullPointerException();
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return linkFirst(node);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offerLast(E e) {
        if (e == null) throw new NullPointerException();
        Node<E> node = new Node<E>(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return linkLast(node);
        } finally {
            lock.unlock();
        }
    }
    public E pollFirst() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return unlinkFirst();
        } finally {
            lock.unlock();
        }
    }

    public E pollLast() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return unlinkLast();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        MyLinkedBlockingDeque<String> myBlockingQueue= new MyLinkedBlockingDeque<String>();
        myBlockingQueue.offerLast("test");
        System.out.println(myBlockingQueue.pollFirst());
        System.out.println(myBlockingQueue.pollLast());
        myBlockingQueue.offerFirst("world");

        myBlockingQueue.offerFirst("hello");
        System.out.println(myBlockingQueue.pollFirst());
        System.out.println(myBlockingQueue.pollLast());
        myBlockingQueue.offerFirst("!");
        myBlockingQueue.offerLast("hanmeimei");
        System.out.println(myBlockingQueue.pollFirst());
        System.out.println(myBlockingQueue.pollLast());
       System.out.println(myBlockingQueue.count);
        myBlockingQueue.offerLast("lilei");
        System.out.println(myBlockingQueue.pollFirst());
        System.out.println(myBlockingQueue.pollLast());
    }
}
