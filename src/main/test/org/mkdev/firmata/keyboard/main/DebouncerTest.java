package org.mkdev.firmata.keyboard.main;

import org.junit.Before;
import org.junit.Test;
import org.mkdev.firmata.keyboard.ut.Callback;
import org.mkdev.firmata.keyboard.ut.Debouncer;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-24
 */
public class DebouncerTest {

    Debouncer<String> debouncer;

    @Before
    public void setUp() throws Exception {
        debouncer = new Debouncer<>(250, new Callback<String>() {
            @Override
            public void call(String arg) {
                System.out.println("OK");
            }
        });
    }

    @Test
    public void testExecute() throws Exception {
        for(int i=0;i<1000;i++) {
            debouncer.execute("x");
            Thread.sleep(1);
        }
    }
}