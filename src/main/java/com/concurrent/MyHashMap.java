package com.concurrent;

import java.util.Objects;

/**
 * @author huangjun
 * @version V1.0
 * @Description: TODO
 * @Date Create in 16:17 2019/10/29
 */
public class MyHashMap<K,V> {

    private int size;

    Node<K,V>[] entry = new Node[16];

    public int getSize() {
        return size;
    }

    public void put(K key, V value){
        size++;
        int index=key.hashCode()&15;
        Node node = new Node(key.hashCode(),key,value,null);
        if (entry[index]==null){

            entry[index]=node;
        }else{
            Node first=entry[index];
            if (first.key.equals(key)){
                first.value=value;
                return;
            }
            while(first.next!=null){

                first=first.next;
                if (first.key.equals(key)){
                    first.value=value;
                    return;
                }
            }
            first.next=node;
        }


    }

    public V get(K key){
        Node node=entry[key.hashCode()&15];
        if (node==null){
            return null;
        }
        if (node.key.equals(key)){
            return (V) node.value;
        }
          Node first=node;
        do{
           if (first.key.equals(key)){

               return (V) first.value;
           }
        }while ((first=first.next)!=null);



        return null;
    }
    public static class Node<K,V>{
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }


    }

    public static void main(String[] args) {
        MyHashMap<String,String> myHashMap = new MyHashMap<String,String>();
        myHashMap.put("hello","world");
        myHashMap.put("hanmeimei","lili");
        myHashMap.put("java","spring");
        myHashMap.put("mysql","oracle");
        myHashMap.put("java","oracle");
        System.out.println(myHashMap.size);
        System.out.println(myHashMap.get("hello"));
        System.out.println(myHashMap.get("hanmeimei"));
        System.out.println(myHashMap.get("java"));
        System.out.println(myHashMap.get("mysql"));
    }
}
