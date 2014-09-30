package org.mkdev.firmata.keyboard.ut;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-29
 */
public class DropperTest {

    Dropper dropper;

    @Before
    public void setUp() throws Exception {
        dropper = new Dropper();
    }

    @Test
    public void testCheck() throws Exception {
        for(int i=0;i<7;i++) {
            spawnThread(i);

            Thread
        }

        for(;;);
    }

    private void spawnThread(final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doWork(i);
                } catch (DropCountExceededException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @LimitRate(value = 5)
    private void doWork(int i) throws DropCountExceededException {
        dropper.checkThread();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println((i+1));

        dropper.releaseThread();
    }
}