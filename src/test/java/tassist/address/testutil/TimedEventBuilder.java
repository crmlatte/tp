package tassist.address.testutil;

import java.time.LocalDateTime;

import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.TimedEvent;

/**
 * A utility class to help with building TimedEvent objects.
 */
public class TimedEventBuilder {
    private String name;
    private String description;
    private LocalDateTime time;

    /**
     * Creates a TimedEventBuilder with the default values.
     */
    public TimedEventBuilder() {
        name = "Test Assignment";
        description = "Test Description";
        time = LocalDateTime.now().plusDays(7);
    }

    /**
     * Sets the {@code name} of the {@code TimedEvent} that we are building.
     */
    public TimedEventBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the {@code description} of the {@code TimedEvent} that we are building.
     */
    public TimedEventBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the {@code time} of the {@code TimedEvent} that we are building.
     */
    public TimedEventBuilder withTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    /**
     * Returns a {@code TimedEvent} with the fields of the {@code TimedEvent} that we are building.
     */
    public TimedEvent build() {
        return new Assignment(name, description, time);
    }
}
