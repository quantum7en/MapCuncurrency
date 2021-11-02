package ru.tuanviet.javabox;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MonitorThread<K, V> implements Runnable {
    private HashMap<K, V> hashMap;
    private List<Map.Entry<K, Long>> valuesList;
    private long ttl;
    private SuperReadWriteLock locker;

    public MonitorThread(HashMap<K, V> map, List<Map.Entry<K, Long>> list, long ttl, SuperReadWriteLock locker){
        this.hashMap = map;
        this.valuesList = list;
        this.ttl = ttl;
        this.locker = locker;
    }

    @Override
    public void run() {

        while (true) {
            locker.acquireReadLock();
            if (valuesList.size() == 0) {
                locker.releaseReadLock();
                System.out.println("Monitor POGIB");
                break;
            }
            Iterator<Map.Entry<K, Long>> iter = valuesList.iterator();
            while(iter.hasNext()) {
                Map.Entry<K, Long> entry = iter.next();
                if (System.currentTimeMillis() - entry.getValue() > ttl) {

                    locker.releaseReadLock();
                    locker.acquireWriteLock();
                    hashMap.remove(entry.getKey());
                    iter.remove();

                    locker.releaseWriteLock();
                    locker.acquireReadLock();
                }
            }
//            for (Map.Entry<K, Long> entry: valuesList) {
//                if (System.currentTimeMillis() - entry.getValue() > ttl) {
//                    locker.releaseReadLock();
//                    locker.acquireWriteLock();
//
//                    hashMap.remove(entry.getKey());
//                    valuesList.remove(entry);
//
//                    locker.releaseWriteLock();
//                    locker.acquireReadLock();
//                }
//            }
            locker.releaseReadLock();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
