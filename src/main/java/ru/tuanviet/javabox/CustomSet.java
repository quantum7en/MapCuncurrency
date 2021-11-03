package ru.tuanviet.javabox;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

public class CustomSet<K, V> extends AbstractSet<Map.Entry<K, V>> {

    private SuperCache<K, V> superCache;
    private SuperReadWriteLock locker;

    public CustomSet(SuperReadWriteLock locker) {

        this.locker = locker;
    }

    public void setSuperCache(SuperCache<K, V> superCache){
        this.superCache = superCache;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new SetIterator<>(superCache.getFirst(), locker);
    }

    @Override
    public int size() {
        locker.releaseReadLock();
        int size = superCache.getMap().size();
        locker.acquireReadLock();
        return size;
    }
}

class SetIterator<K, V> implements Iterator<Map.Entry<K, V>> {

    private SuperReadWriteLock locker;
    private CacheItem<K, V> first;

    public SetIterator(CacheItem<K, V> first, SuperReadWriteLock locker) {
        System.out.println("in constr");
        this.locker = locker;
        this.first = first;
    }

    @Override
    public boolean hasNext() {
        //lock
        //System.out.println("in has next1");
        locker.acquireReadLock();
        //System.out.println("in has next2");
        if ( first == null ) {
            locker.releaseReadLock();
            return false;
        }
        return true;
    }

    @Override
    public Map.Entry<K, V> next() {
        // unlock
        //System.out.println("in next1");
        CacheItem<K, V> node = first;
        first = first.getNext();
        locker.releaseReadLock();
        //System.out.println("after release in next");

        return node;
    }
}
