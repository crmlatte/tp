package tassist.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.Assignment;

/**
 * Jackson-friendly version of {@link TimedEvent}.
 */
class JsonAdaptedTimedEvent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "TimedEvent's %s field is missing!";

    private final String name;
    private final String description;
    private final String time;
    private final String type;

    /**
     * Constructs a {@code JsonAdaptedTimedEvent} with the given timed event details.
     */
    @JsonCreator
    public JsonAdaptedTimedEvent(@JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("time") String time,
            @JsonProperty("type") String type) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.type = type;
    }

    /**
     * Converts a given {@code TimedEvent} into this class for Jackson use.
     */
    public JsonAdaptedTimedEvent(TimedEvent source) {
        name = source.getName();
        description = source.getDescription();
        time = source.getTime().toString();
        type = source.getClass().getSimpleName();
    }

    /**
     * Converts this Jackson-friendly adapted timed event object into the model's {@code TimedEvent} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted timed event.
     */
    public TimedEvent toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }

        if (description == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "description"));
        }

        if (time == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "time"));
        }

        if (type == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "type"));
        }

        try {
            java.time.LocalDateTime parsedTime = java.time.LocalDateTime.parse(time);
            switch (type) {
                case "Assignment":
                    return new Assignment(name, description, parsedTime);
                default:
                    throw new IllegalValueException("Invalid timed event type: " + type);
            }
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalValueException("Invalid date format: " + time);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
} 