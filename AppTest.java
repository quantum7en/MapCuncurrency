package ru.tuanviet.javabox;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class AppTest {
    private static final Long timeForReading = 2000l;
    private static final Long timeForWritting = 2000l;
    private static final SuperReadWriteLock lock = new SuperReadWriteLock();
    private static String message = "D";

    @Test
    public void shouldReadersWorkParallel() throws InterruptedException {
        Long timeBefore = System.currentTimeMillis();
        Long timeAfter;

        testOnlyReaders();
        timeAfter = System.currentTimeMillis();
        Long resultTime = timeAfter-timeBefore;

        assertThat(resultTime).isLessThan(timeForReading + 500);
    }

    @Test
    public void shouldWritersWorkSerially() throws InterruptedException {
        Long timeBefore = System.currentTimeMillis();
        Long timeAfter;

        testOnlyWriters();
        timeAfter = System.currentTimeMillis();
        Long resultTime = timeAfter-timeBefore;

        assertThat(resultTime).isGreaterThan(timeForWritting*3 - 1);
    }


    private static void testOnlyReaders() throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(new Reader());
            threads[i].setName("Reader " + i);
            threads[i].start();
        }
        for (int i = 0; i < 5; i++) {
            threads[i].join();
        }
    }

    private static void testOnlyWriters() throws InterruptedException {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(new WriterA());
            threads[i].setName("WriterA " + i);
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }
    }

    static class Reader implements Runnable {

        public void run() {

            lock.acquireReadLock();

            try {
                Long duration = timeForReading;
                System.out.println(Thread.currentThread().getName()
                        + "  Time Taken " + (duration / 1000) + " seconds.");
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + ": " + message);
                lock.releaseReadLock();
            }
        }
    }
    static class WriterA implements Runnable {

        public void run() {
            lock.acquireWriteLock();

            try {
                Long duration = timeForWritting;
                System.out.println(Thread.currentThread().getName()
                        + "  Time Taken " + (duration / 1000) + " seconds.");
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                message = message.concat("a");
                lock.releaseWriteLock();
            }
        }
    }
}
