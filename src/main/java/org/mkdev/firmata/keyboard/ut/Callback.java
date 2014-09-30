package org.mkdev.firmata.keyboard.ut;

/**
 * @author Michał Kołodziejski &lt;<I><A href="mailto:michal.kolodziejski@gmail.com">michal.kolodziejski@gmail.com</A></I>&gt;
 */
public interface Callback<T> {
  public void call(T arg);
}