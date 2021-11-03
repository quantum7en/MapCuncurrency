package ru.tuanviet.javabox;

import java.util.*;
import java.util.function.Supplier;

public class SuperCache<K, V> implements Map<K, V> {
    private final int MIN_MAXSIZE = 1;
    private final long LATENCY = 200;

    private SuperReadWriteLock locker;
    private Map<K, CacheItem<K, V>> map;

    private CacheItem<K, V> first, last;
    private CustomSet<K, V> customSet;

    private int size;
    private final int DEFAULT_CAPACITY = 16;
    private int CAPACITY;
    private Thread monitor;
    private long ttl;
    private int maxSize;

    public SuperCache(long ttl) {

        this.ttl = ttl;
        this.maxSize = -1;
        map = new HashMap<>(DEFAULT_CAPACITY);

        locker = new SuperReadWriteLock();
        customSet = new CustomSet<>(locker);
        //   monitor = new Thread(new MonitorThread<>(this, map, ttl, locker));
    }

    public SuperCache(long ttl, int maxSize) {
        if ( ttl <= LATENCY || maxSize < MIN_MAXSIZE ) {
            throw new IllegalArgumentException("Latency should be more than "
                    + LATENCY
                    + " or max size "
                    + MIN_MAXSIZE);
        }
        this.ttl = ttl;
        this.maxSize = maxSize;
        map = new HashMap<>(maxSize); //? max size

        locker = new SuperReadWriteLock();
        customSet = new CustomSet<>(locker);
        //   monitor = new Thread(new MonitorThread<>(this, map, ttl, locker));
    }

    V getOrCompute(K key, Supplier<V> valueSupplier) {
        V value;
        //lock
        if ( !map.containsKey(key) ) {
            value = put(key, valueSupplier.get());
        } else {
            value = map.get(key).getValue();
        }
        //unlock
        return value;
    }

    public CacheItem<K, V> getFirst() {
        return first;
    }

    public Map<K, CacheItem<K, V>> getMap() {
        return map;
    }

    @Override
    public int size() {
        locker.acquireReadLock();
        int size = map.size();
        locker.releaseReadLock();
        return size;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object value) {
        Iterator<Entry<K, V>> i = this.entrySet().iterator();
        Entry<K, V> e;
        if ( value == null ) {
            while (i.hasNext()) {
                e = i.next();
                if ( e.getValue() == null ) {
                    return true;
                }
            }
        } else {
            while (i.hasNext()) {
                e = i.next();
                if ( value.equals(e.getValue()) ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(Object key) {
        if ( !map.containsKey(key) ) {
            return null;
        }
        CacheItem<K, V> node = map.get(key);
        //   node.updateCreationTime();
        reorder(node);
        return node.getValue();
    }

    @Override
    public V put(K key, V value) {

        //       System.out.println("in put method");
        CacheItem<K, V> node = new CacheItem<>(key, value);
        //       System.out.println("after creation");
        if ( map.containsKey(key) == false ) {
            //          System.out.println("before size");

            if ( size() == maxSize ) {
                //             System.out.println("in if ");
                locker.acquireWriteLock();
                deleteNode(first);
                locker.releaseWriteLock();
            }
            //        System.out.println("add node to last");
            locker.acquireWriteLock();
            //      System.out.println("not lock");
            addNodeToLast(node);
            if (monitor == null) {
                monitor = new Thread(new MonitorThread<>(this, map, ttl, locker));
            }
            if(map.size() == 1){
                monitor.start();
            }
            //      System.out.println("after addNodeToLast(node)");
            locker.releaseWriteLock();
        }

        //      System.out.println("in put method if doesnt contain");

        locker.acquireWriteLock();
        map.put(key, node);

        locker.releaseWriteLock();
        return value;
    }

    @Override
    public V remove(Object key) {
        locker.acquireWriteLock();
        V res = deleteNode(map.get(key));
        locker.releaseWriteLock();

        return res;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        int i = 0;

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            if ( i > maxSize ) {
                break;
            }
            put(entry.getKey(), entry.getValue());
            i++;
        }
    }

    @Override
    public void clear() {
        map.clear();
        first = null;
        last = null;
    }

    @Override
    public Set<K> keySet() {
        locker.acquireReadLock();

        Set<K> setKey = new HashSet<>(this.map.keySet());

        locker.releaseReadLock();
        return setKey;
    }

    @Override
    public Collection<V> values() {
        locker.acquireReadLock();
        Collection<V> collect = new ArrayList<>();

        for (Map.Entry<K, CacheItem<K, V>> entry : map.entrySet()) {
            collect.add(entry.getValue().getValue());
        }
        locker.releaseReadLock();
        return collect;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        locker.acquireReadLock();
        System.out.println("Entry");

        // CopyOnWriteArraySet<Entry<K, V>> setRes = new CopyOnWriteArraySet<>(hashMap.entrySet());
        customSet.setSuperCache(this);
        Set<Entry<K, V>> setRes = customSet;

        locker.releaseReadLock();
        System.out.println(map.size());
        return setRes;
    }

    private V deleteNode(CacheItem<K, V> node) {
        if ( node == null ) {
            return null;
        }
        if ( last == node ) {
            last = node.getPrev();
        } else if ( first == node ) {
            first = node.getNext();
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        V value = (V) map.remove(node.getKey()).getValue();
        node = null; // Optional, collected by GC
        size--;
        return value;
    }

    private void addNodeToLast(CacheItem<K, V> node) {
        if ( last != null ) {
            last.setNext(node);
            node.setPrev(last);
        }

        last = node;
        if ( first == null ) {
            first = node;
        }
        size++;
    }

    private void addNodeToFirst(CacheItem<K, V> node) {
        if ( first != null ) {
            node.setNext(first);
            first.setPrev(node);
        }
        first = node;

        if ( last == null ) {
            last = node;
        }
        size++;
    }

    private void reorder(CacheItem<K, V> node) {
        if ( last == node ) {
            return;
        }
        CacheItem<K, V> nextNode = node.getNext();
        while (nextNode != null) {
            if ( nextNode.getCreationTime() < node.getCreationTime() ) {
                break;
            }
            if ( first == node ) {
                first = nextNode;
            }
            if ( node.getPrev() != null ) {
                node.getPrev().setNext(nextNode);
            }
            nextNode.setPrev(node.getPrev());
            node.setPrev(nextNode);
            node.setNext(nextNode.getNext());
            if ( nextNode.getNext() != null ) {
                nextNode.getNext().setPrev(node);
            }
            nextNode.setNext(node);
            nextNode = node.getNext();
        }
        if ( node.getNext() == null ) {
            last = node;
        }
    }

}
