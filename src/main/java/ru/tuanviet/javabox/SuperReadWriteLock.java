package ru.tuanviet.javabox;

import java.util.ArrayList;
import java.util.List;

public class SuperReadWriteLock {
    private int countWriters;
    private int countReaders;

    public SuperReadWriteLock() {
        countReaders = 0;
        countWriters = 0;
    }

    public synchronized void acquireReadLock() {
        while (countWriters > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        countReaders++;
    }

    public synchronized void releaseReadLock() {
        countReaders--;
        notify();
    }

    public synchronized void acquireWriteLock() {

        while (countWriters > 0 || countReaders > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        countWriters++;
    }

    public synchronized void releaseWriteLock() {
        countWriters--;
        notify();
    }

}
