package ru.itis.exceptions;

import java.util.Arrays;

public class IllegalMessageTypeException extends IllegalArgumentException {
    private final static String MESSAGE = "Wrong message type!";
    public IllegalMessageTypeException() {
        super(MESSAGE);
    }
}
