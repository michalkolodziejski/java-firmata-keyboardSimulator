package org.mkdev.firmata.keyboard.ut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 * @version 1.0
 * @license: GPLv3 (http://www.gnu.org/licenses/gpl-3.0.txt)
 * @since: 2014-09-29
 */
public class Dropper {

    private ConcurrentHashMap<Method, VO> channelCount = new ConcurrentHashMap<>();

    public void releaseThread() throws DropCountExceededException {
        Method callingMethod = queryCallingMethod();

        if (channelCount.get(callingMethod).getThreadCount().get() == 0) {
            return;
        }

        channelCount.get(callingMethod).getThreadCount().decrementAndGet();

        System.out.println("threads in queue: " + channelCount.get(callingMethod).getThreadCount().get());
    }

    private Method queryCallingMethod() throws DropCountExceededException {
        Method callingMethod;
        try {
            callingMethod = getCallingMethodWithAnnotation();
        } catch (ClassNotFoundException e) {
            throw new DropCountExceededException("Calling class not found!");
        }

        if (callingMethod == null) {
            throw new DropCountExceededException("Calling method has not been annotated!");
        }
        return callingMethod;
    }

    private Method getCallingMethodWithAnnotation() throws ClassNotFoundException {
        final Thread t = Thread.currentThread();
        final StackTraceElement[] stackTrace = t.getStackTrace();

        for (StackTraceElement stackTraceElement : stackTrace) {
            Class<?> clazz = Class.forName(stackTraceElement.getClassName());

            for (Method candidate : clazz.getDeclaredMethods()) {
                for (Annotation annotation : candidate.getDeclaredAnnotations()) {
                    if (annotation instanceof LimitRate) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }

    public synchronized void checkThread() throws DropCountExceededException {

       this.checkThread(null);
    }

    public synchronized void checkThread(Callback callback) throws DropCountExceededException {

        Method callingMethod = queryCallingMethod();

//        System.out.println("method ="+callingMethod+" :" + channelCount.containsKey(callingMethod));

        if (!channelCount.containsKey(callingMethod)) {
            channelCount.put(callingMethod, new VO(callingMethod.getAnnotation(LimitRate.class).value()));
        }

//        System.out.println("["+callingMethod+"] maxCount:" + channelCount.get(callingMethod).getMaxCount());
//        System.out.println("["+callingMethod+"] threadCount:" + channelCount.get(callingMethod).getThreadCount());

        if (channelCount.get(callingMethod).getThreadCount().get() < channelCount.get(callingMethod).getMaxCount()) {
            channelCount.get(callingMethod).getThreadCount().incrementAndGet();
        } else {
            if (callback == null) {
                throw new DropCountExceededException();
            } else {
                callback.call();
            }
        }
    }

    private class VO {
        private int maxCount;
        private AtomicInteger threadCount;

        public VO(int maxCount) {
            this.maxCount = maxCount;
            threadCount = new AtomicInteger();
        }

        public int getMaxCount() {
            return maxCount;
        }

        public AtomicInteger getThreadCount() {
            return threadCount;
        }
    }
}
