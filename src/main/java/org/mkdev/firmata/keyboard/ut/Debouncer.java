package org.mkdev.firmata.keyboard.ut;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-24
 */
public class Debouncer<T> {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final ConcurrentHashMap<T, DebounceTask> map = new ConcurrentHashMap<>();

    private Callback<T> callback;

    private long debounceDelay = 0;

    public Debouncer(long debounceDelay, Callback<T> callback) {
        this.callback = callback;
        this.debounceDelay = debounceDelay;
    }

    public void execute(T key) {
        DebounceTask task = new DebounceTask(key);
        DebounceTask previous = map.putIfAbsent(key, task);

        if (previous == null) {
            executor.schedule(task, debounceDelay, TimeUnit.MILLISECONDS);
        }
    }

    private class DebounceTask extends TimerTask {
        private final T key;
        private final Object lock = new Object();
        private long lastHit = System.currentTimeMillis();

        public DebounceTask(T key) {
            this.key = key;
        }

        @Override
        public void run() {
            synchronized (lock) {
                if ((System.currentTimeMillis() - lastHit) > debounceDelay) {
                    try {
                        callback.call(key);

                        lastHit = System.currentTimeMillis();
                    } finally {
                        map.remove(key);
                    }
                }
            }
        }
    }
}