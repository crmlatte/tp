package tassist.address.model.timedevents.exceptions;

/**
 * Signals that the operation is unable to find the specified timed event.
 */
public class TimedEventNotFoundException extends RuntimeException {
    public TimedEventNotFoundException() {
        super("Timed event not found");
    }
}