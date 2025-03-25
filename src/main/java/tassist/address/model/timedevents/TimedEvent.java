package tassist.address.model.timedevents;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import tassist.address.commons.util.ToStringBuilder;

/**
 * Represents a TimedEvent in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public abstract class TimedEvent {
    // Identity fields
    private final String name;
    private final String description;
    private final LocalDateTime time;

    /**
     * Every field must be present and not null.
     */
    public TimedEvent(String name, String description, LocalDateTime time) {
        requireNonNull(name);
        requireNonNull(description);
        requireNonNull(time);
        this.name = name;
        this.description = description;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Returns true if both timed events have the same name and time.
     * This defines a weaker notion of equality between two timed events.
     */
    public boolean isSameTimedEvent(TimedEvent otherTimedEvent) {
        if (otherTimedEvent == this) {
            return true;
        }

        return otherTimedEvent != null
                && otherTimedEvent.getName().equals(getName())
                && otherTimedEvent.getTime().equals(getTime());
    }

    /**
     * Returns true if both timed events have the same identity and data fields.
     * This defines a stronger notion of equality between two timed events.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimedEvent)) {
            return false;
        }

        TimedEvent otherTimedEvent = (TimedEvent) other;
        return otherTimedEvent.getName().equals(getName())
                && otherTimedEvent.getDescription().equals(getDescription())
                && otherTimedEvent.getTime().equals(getTime());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("description", description)
                .add("time", time)
                .toString();
    }
} 