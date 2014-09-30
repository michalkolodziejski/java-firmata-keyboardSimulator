package org.mkdev.firmata.keyboard.ut;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-29
 */
public class Throttler<T> {
    private long frequency;

    protected long lastSchedAction = System.currentTimeMillis();

    /**
     *
     * @param perSecondRate in milliseconds
     * @throws Exception
     */
    public Throttler(float perSecondRate) throws Exception {
        if(perSecondRate <= 0.0f) {
            throw new InvalidArgumentException(new String[]{"perSecondRate", "Invalid rate"});
        }

        this.frequency = (long)(1000.0f / perSecondRate);
    }

    /**
     *
     * @throws InterruptedException
     */
    public void awaitPermit(T arg) throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        long timeLeft;

        synchronized(this) {
            timeLeft = lastSchedAction + frequency - currentTime;
            if(timeLeft > 0) {
                lastSchedAction += frequency;
            }
            else {
                lastSchedAction = currentTime;
            }
        }

        Thread.sleep((timeLeft>0)?timeLeft:0);
    }
}