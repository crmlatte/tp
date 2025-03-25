package tassist.address.model.timedevents;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an Assignment in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Assignment extends TimedEvent {
    /**
     * Every field must be present and not null.
     */
    public Assignment(String name, String description, LocalDateTime time) {
        super(name, description, time);
    }

    @Override
    public String toString() {
        return String.format("Assignment: %s (Due: %s)", 
            getName(), 
            getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
    }
} 