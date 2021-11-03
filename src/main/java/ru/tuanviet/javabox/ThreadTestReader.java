package ru.tuanviet.javabox;

import java.util.Map;

public class ThreadTestReader implements Runnable{

    private SuperCache<String, Integer> threadSuperCache;

    public ThreadTestReader(SuperCache<String, Integer> threadSuperCache) {
        this.threadSuperCache = threadSuperCache;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            for (Map.Entry<String, Integer> entry: threadSuperCache.entrySet()) { //except
                System.out.println("read in thread " + entry.getKey() + " " + entry.getValue());
            }
        }
    }
}
