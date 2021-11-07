package ru.tuanviet.javabox;

import java.util.AbstractMap;
import java.util.Map;

public class MonitorThread<K, V> implements Runnable {
    private final Map<K, CacheItem<K, V>> hashMap;
    private final SuperCache<K, V> superCache;
    private final long ttl;
    private final SuperReadWriteLock locker;

    public MonitorThread(SuperCache<K, V> superCache, Map<K, CacheItem<K, V>> map, long ttl, SuperReadWriteLock locker) {
        this.superCache = superCache;
        this.hashMap = map;
        this.ttl = ttl;
        this.locker = locker;
    }


    @Override
    public void run() {

        CacheItem<K, V> node = superCache.getFirst();
        while (true) {
            locker.acquireReadLock();
            System.out.println(hashMap.size() + " in monitor");
            hashMap.clear();
            AbstractMap
//            if ( hashMap.size() == 0 ) {
//                locker.releaseReadLock();
//                System.out.println("Monitor POGIB");
//                break;
//            }
//            CacheItem<K, V> node = superCache.getFirst();
//            if ( System.currentTimeMillis() - node.getCreationTime() > ttl ) {
//
//            }

//            for (Map.Entry<K, V> entry : superCache.entrySet()) {
//                if ( System.currentTimeMillis() - superCache.get().getCreationTime() > ttl ) {
//                    System.out.println("in monitor dead");
//                    locker.releaseReadLock();
//
//                    System.out.println("before remove");
//                    superCache.remove(entry.getKey());
//
//                    //hashMap.remove(entry.getKey());
//                    System.out.println("after remove");
//
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

