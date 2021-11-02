package ru.tuanviet.javabox;

import java.util.HashMap;

public class ThreadTest implements Runnable{
    private SuperCache<String, Integer> threadSuperCache;

    public ThreadTest(SuperCache<String, Integer> superCache){
        threadSuperCache = superCache;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {

            threadSuperCache.put(String.valueOf(i) + " " + Thread.currentThread().getName(), i);
        }
    }

}
