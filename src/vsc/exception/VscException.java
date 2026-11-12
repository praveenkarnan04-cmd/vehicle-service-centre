package vsc.exception;

/** Base checked exception for all business-rule problems in the system. */
// OOP: EXCEPTION HANDLING - custom base exception; all the other exceptions extend it.
public class VscException extends Exception {
    public VscException(String message) { super(message); }
    public VscException(String message, Throwable cause) { super(message, cause); }
}
