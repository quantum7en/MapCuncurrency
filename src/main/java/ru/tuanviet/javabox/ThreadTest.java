package ru.tuanviet.javabox;

import java.util.HashMap;

public class ThreadTest implements Runnable{
    private SuperCache<String, Integer> threadSuperCache;

    public ThreadTest(SuperCache<String, Integer> superCache){
        threadSuperCache = superCache;
    }

    @Override
    public void run() {
        for (int i = 100; i < 110; i++) {
            System.out.println("put");
            threadSuperCache.put(String.valueOf(i) + " " + Thread.currentThread().getName(), i);
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
    }

}
