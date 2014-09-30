package org.mkdev.firmata.keyboard.main;

import org.junit.Before;
import org.junit.Test;
import org.mkdev.firmata.keyboard.ut.Throttler;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-24
 */
public class ThrottlerTest {

    Throttler<Integer> throttler;

    @Before
    public void setUp() throws Exception {
        throttler = new Throttler<>(1);
    }

    @Test
    public void test() throws Exception {

        for (int i = 0; i < 10; i++) {

            spawnThread(i);
        }

        for(;;);
    }

    private void spawnThread(final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    throttler.awaitPermit(i);

                    System.out.println((i + 1));

                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}