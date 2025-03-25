package tassist.address.model.timedevents.exceptions;

/**
 * Signals that the operation will result in duplicate TimedEvents (TimedEvents are considered duplicates if they have the same name and time).
 */
public class DuplicateTimedEventException extends RuntimeException {
    public DuplicateTimedEventException() {
        super("Operation would result in duplicate timed events");
    }
}