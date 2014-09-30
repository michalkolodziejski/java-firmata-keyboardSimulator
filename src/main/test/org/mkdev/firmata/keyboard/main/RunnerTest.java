package org.mkdev.firmata.keyboard.main;

import org.junit.Before;
import org.junit.Test;
import org.mkdev.firmata.keyboard.ut.Runner;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-24
 */
public class RunnerTest {

    Runner<String> runner;

    @Before
    public void setUp() throws Exception {
        runner = new Runner<String>(1);
    }

    @Test
    public void test() throws Exception {

        for (int i = 0; i < 10; i++) {
            runner.awaitPermit("x");

            System.out.println("tick x");
        }

        runner.terminate();
    }
}