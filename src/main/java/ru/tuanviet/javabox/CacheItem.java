package ru.tuanviet.javabox;

import java.util.Map;

public class CacheItem<K, V> implements Map.Entry<K, V>{
    private final K key;
    private final V value;
    private long creationTime = 0;
    private CacheItem<K, V> prev, next;

    public CacheItem(K key, V value) {
        this.value = value;
        this.key = key;
        this.creationTime = System.currentTimeMillis();
    }

    public void updateCreationTime() {
        this.creationTime = System.currentTimeMillis();
    }

    public K getKey() {
        this.updateCreationTime();
        return key;
    }

    public V getValue() {
        this.updateCreationTime();
        return value;
    }

    @Override
    public V setValue(V v) {
        return null;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public CacheItem<K, V> getPrev() {
        return prev;
    }

    public CacheItem<K, V> getNext() {
        return next;
    }

    public void setNext(CacheItem<K, V> node) {
        this.next = node;
    }

    public void setPrev(CacheItem<K, V> node) {
        this.prev = node;
    }
}
