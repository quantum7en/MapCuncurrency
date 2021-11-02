package ru.tuanviet.javabox;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

public class SuperCache<K, V> implements Map <K, V> {
    private final int MIN_MAXSIZE = 1;
    private final long LATENCY = 200;

    private  SuperReadWriteLock locker;
    private HashMap<K, V> hashMap;
    private List <Map.Entry<K, Long>> valuesList;
    private Thread monitor;
    private long ttl;
    private int maxSize;

    public SuperCache(long ttl){
        this.ttl = ttl;
        this.maxSize = -1;
        hashMap = new HashMap<>();
//        ttlMap = new HashMap<>();
        valuesList = new ArrayList<>();
        locker = new SuperReadWriteLock();
        monitor = new Thread(new MonitorThread<>(hashMap, valuesList, ttl, locker));
        CopyOnWriteArraySet<Entry<K, V>> setRes = new CopyOnWriteArraySet<>(hashMap.entrySet());
    }

    public SuperCache(long ttl, int maxSize){
        if (ttl <= LATENCY || maxSize < MIN_MAXSIZE){
            throw new IllegalArgumentException("Latency should be more than "
                    + LATENCY
                    + " or max size "
                    + MIN_MAXSIZE);
        }
        this.ttl = ttl;
        this.maxSize = maxSize;
        hashMap = new HashMap<>();
        //ttlMap = new HashMap<>();
        valuesList = new ArrayList<>();
        locker = new SuperReadWriteLock();
        monitor = new Thread(new MonitorThread<>(hashMap, valuesList, ttl, locker));
    }

    V getOrCompute(K key, Supplier<V> valueSupplier){
        V value;
        //lock
        //(key, valueSupplier)->(hashMap.containsKey(key),  );
        if (!hashMap.containsKey(key)) {
            value = put(key, valueSupplier.get());
        }
        else {
            value = hashMap.get(key);
        }
        //unlock
        return value ;
    }

    @Override
    public int size() {
        locker.releaseReadLock();
        int size = hashMap.size();
        locker.acquireReadLock();
        return size;
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return hashMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return hashMap.containsValue(o);
    }

    @Override
    public V get(Object o) {
        return hashMap.get(o);
    }

    @Override
    public V  put(K k, V v) {
        locker.acquireWriteLock();
        if (hashMap.size() == this.maxSize) {
            Collections.sort(valuesList, new Comparator<Map.Entry<K, Long>>() {
                @Override
                public int compare(Map.Entry<K, Long> o1, Map.Entry<K, Long> o2) {
                    return o1.getValue().compareTo(o2.getValue());
                }
            });
            Entry<K, Long> entry = valuesList.get(0);
            hashMap.remove(entry.getKey());
            valuesList.remove(entry);
        }
        Entry<K, Long> entry = Map.entry(k, System.currentTimeMillis());
        valuesList.add(entry);
        V value = hashMap.put(k, v);
        if (hashMap.size() == 1) {
            monitor.start();
        }
        locker.releaseWriteLock();
        return value;
    }

    @Override
    public V remove(Object o) {
        locker.acquireWriteLock();
        valuesList.remove(o);
        V res = hashMap.remove(o);
        locker.releaseWriteLock();
        //ttlMap.remove(o);
        return res;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        valuesList.clear();
       // ttlMap.clear();
        hashMap.clear();
    }

    @Override
    public Set<K> keySet() {
        locker.acquireReadLock();
        Set<K> setKey = hashMap.keySet();
        locker.releaseReadLock();
        return setKey;
    }

    @Override
    public Collection<V> values() {
        locker.acquireReadLock();
        Collection<V> collect = hashMap.values();
        locker.releaseReadLock();
        return collect;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        locker.acquireReadLock();
        System.out.println("Entry");

       // CopyOnWriteArraySet<Entry<K, V>> setRes = new CopyOnWriteArraySet<>(hashMap.entrySet());

        Set<Entry<K, V>> setRes = hashMap.entrySet();

        locker.releaseReadLock();
        System.out.println(hashMap.size());
        return setRes;
    }
}
