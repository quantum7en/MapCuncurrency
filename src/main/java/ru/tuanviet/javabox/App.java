package ru.tuanviet.javabox;

import java.util.Map;

public class App {

    private static final SuperReadWriteLock lock = new SuperReadWriteLock();
    private static String message = "D";

    public static void main(String[] args) throws InterruptedException {
        SuperCache<String, Integer>  superCache = new SuperCache<>(3000);

        Thread[] arr = new Thread[4];
        for(int i = 0 ; i < 4; i+=2 ){
            arr[i] = new Thread(new ThreadTest(superCache));
            arr[i+1] = new Thread(new ThreadTestReader(superCache));
            arr[i].start();
            Thread.sleep(100);
            arr[i+1].start();
        }

        System.out.println("Started");
//        for (int i = 0; i < 3; i++) {
//            arr[i].join();
//        }

 //       superCache.put("main", 9);
        Thread.sleep(3500);
        for (Map.Entry<String, Integer> entry: superCache.entrySet()) { //except
            System.out.println(entry.getKey() + " " + entry.getValue());
            System.out.println("here");
        }
        System.out.println("PAUSE");
//        for (Map.Entry<String, Integer> entry: superCache.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }
//        int i = 0;
//        for (Map.Entry<Integer, String> entry: superCache.entrySet()) {
//            if(i == 3){
//                superCache.remove(entry);
//            }
//            i++;
//        }

//        Thread.sleep(3000);
//        for (Map.Entry<Integer, String> entry: superCache.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

    }










//    private static void testOnlyReaders() throws InterruptedException {
//        Thread[] threads = new Thread[5];
//        for (int i = 0; i < 5; i++) {
//            threads[i] = new Thread(new Reader());
//            threads[i].setName("Reader " + i);
//            threads[i].start();
//        }
//        for (int i = 0; i < 5; i++) {
//            threads[i].join();
//        }
//
//    }
//
//    private static void testOnlyWriters() throws InterruptedException {
//        Thread[] threads = new Thread[3];
//        for (int i = 0; i < 3; i++) {
//            threads[i] = new Thread(new WriterA());
//            threads[i].setName("WriterA " + i);
//            threads[i].start();
//        }
//        for (int i = 0; i < 3; i++) {
//            threads[i].join();
//        }
//    }
//
//    private static void testWritersAndReaders() throws InterruptedException {
//        Thread[] threadsReaders = new Thread[6];
//
//        for (int i = 0; i < 3; i++) {
//            threadsReaders[i] = new Thread(new Reader());
//            threadsReaders[i].setName("Reader " + i);
//            threadsReaders[i].start();
//        }
//
//        Thread treadOneWriter = new Thread(new WriterA());
//        treadOneWriter.setName("SingleWriter");
//        treadOneWriter.start();
//        Thread.sleep(3000);
//
//        for (int i = 3; i < 6; i++) {
//            threadsReaders[i] = new Thread(new Reader());
//            threadsReaders[i].setName("Reader " + i);
//            threadsReaders[i].start();
//        }
//
//        for (int i = 0; i < 6; i++) {
//            threadsReaders[i].join();
//        }
//        treadOneWriter.join();
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        testWritersAndReaders();
////        testOnlyWriters();
////        testOnlyReaders();
//        System.out.println(message);
//
//    }
//
//    static class Reader implements Runnable {
//
//        public void run() {
//
//            lock.acquireReadLock();
//
//            try {
//                Long duration = (long) (Math.random() * 1000);
//                System.out.println(Thread.currentThread().getName()
//                        + "  Time Taken " + (duration / 1000) + " seconds.");
//                Thread.sleep(duration);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                System.out.println(Thread.currentThread().getName() + ": " + message);
//                lock.releaseReadLock();
//            }
//        }
//    }
//
//    static class WriterA implements Runnable {
//
//        public void run() {
//            lock.acquireWriteLock();
//
//            try {
//                Long duration = (long) (Math.random() * 10000);
//                System.out.println(Thread.currentThread().getName()
//                        + "  Time Taken " + (duration / 1000) + " seconds.");
//                Thread.sleep(duration);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                message = message.concat("a");
//                lock.releaseWriteLock();
//            }
//        }
//    }
//
//    static class WriterB implements Runnable {
//
//        public void run() {
//            lock.acquireWriteLock();
//
//            try {
//                Long duration = (long) (Math.random() * 10000);
//                System.out.println(Thread.currentThread().getName()
//                        + "  Time Taken " + (duration / 1000) + " seconds.");
//                Thread.sleep(duration);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                message = message.concat("b");
//                lock.releaseWriteLock();
//            }
//        }
//    }
}
