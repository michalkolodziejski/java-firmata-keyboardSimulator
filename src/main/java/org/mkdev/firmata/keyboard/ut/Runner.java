package org.mkdev.firmata.keyboard.ut;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mkolodziejski on 28.09.2014.
 */
public class Runner<T> {
    private long frequency;

    protected long lastSchedAction = System.currentTimeMillis();
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Object lock = new Object();

    /**
     * @param perSecondRate in milliseconds
     * @throws Exception
     */
    public Runner(float perSecondRate) throws Exception {
        this.frequency = (long) (1000.0f / perSecondRate);

        executorService.scheduleWithFixedDelay(() -> {
            System.out.println("unlock");
//            lock.notify();
        }, 0, frequency, TimeUnit.MILLISECONDS);
    }

    /**
     * @throws InterruptedException
     */
    public void awaitPermit(T arg) throws InterruptedException, ExecutionException {
//        synchronized (lock) {
//            lock.wait();
//        }
    }

    public void terminate() {
        executorService.shutdown();
    }

}
