package org.mkdev.firmata.keyboard.ut;

/**
 * Created by mkolodziejski on 29.09.2014.
 */
public class DropCountExceededException extends Exception {

    private static final long serialVersionUID = 2253293215063842044L;

    public DropCountExceededException(ClassNotFoundException e) {
        super(e);
    }

    public DropCountExceededException() {
        super();
    }

    public DropCountExceededException(String s) {
        super(s);
    }
}
