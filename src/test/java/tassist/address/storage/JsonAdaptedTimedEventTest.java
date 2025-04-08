package tassist.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tassist.address.model.timedevents.TimedEvent.MESSAGE_NAME_CONSTRAINTS;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalTimedEvents.ASSIGNMENT_1;

import org.junit.jupiter.api.Test;

import tassist.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedTimedEventTest {
    private static final String INVALID_NAME = "";
    private static final String INVALID_TIME = "invalid-time";
    private static final String INVALID_TYPE = "InvalidType";

    private static final String VALID_NAME = ASSIGNMENT_1.getName();
    private static final String VALID_DESCRIPTION = ASSIGNMENT_1.getDescription();
    private static final String VALID_TIME = ASSIGNMENT_1.getTime().toString();
    private static final String VALID_TYPE = ASSIGNMENT_1.getClass().getSimpleName();

    @Test
    public void toModelType_validTimedEventDetails_returnsTimedEvent() throws Exception {
        JsonAdaptedTimedEvent timedEvent = new JsonAdaptedTimedEvent(ASSIGNMENT_1);
        assertEquals(ASSIGNMENT_1, timedEvent.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent =
                new JsonAdaptedTimedEvent(INVALID_NAME, VALID_DESCRIPTION, VALID_TIME, VALID_TYPE);
        assertThrows(IllegalValueException.class, MESSAGE_NAME_CONSTRAINTS, timedEvent::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent = new JsonAdaptedTimedEvent(null, VALID_DESCRIPTION, VALID_TIME, VALID_TYPE);
        String expectedMessage = String.format(JsonAdaptedTimedEvent.MISSING_FIELD_MESSAGE_FORMAT, "name");
        assertThrows(IllegalValueException.class, expectedMessage, timedEvent::toModelType);
    }

    @Test
    public void toModelType_nullDescription_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent = new JsonAdaptedTimedEvent(VALID_NAME, null, VALID_TIME, VALID_TYPE);
        String expectedMessage = String.format(JsonAdaptedTimedEvent.MISSING_FIELD_MESSAGE_FORMAT, "description");
        assertThrows(IllegalValueException.class, expectedMessage, timedEvent::toModelType);
    }

    @Test
    public void toModelType_invalidTime_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent =
                new JsonAdaptedTimedEvent(VALID_NAME, VALID_DESCRIPTION, INVALID_TIME, VALID_TYPE);
        assertThrows(IllegalValueException.class, timedEvent::toModelType);
    }

    @Test
    public void toModelType_nullTime_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent = new JsonAdaptedTimedEvent(VALID_NAME, VALID_DESCRIPTION, null, VALID_TYPE);
        String expectedMessage = String.format(JsonAdaptedTimedEvent.MISSING_FIELD_MESSAGE_FORMAT, "time");
        assertThrows(IllegalValueException.class, expectedMessage, timedEvent::toModelType);
    }

    @Test
    public void toModelType_invalidType_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent =
                new JsonAdaptedTimedEvent(VALID_NAME, VALID_DESCRIPTION, VALID_TIME, INVALID_TYPE);
        assertThrows(IllegalValueException.class, timedEvent::toModelType);
    }

    @Test
    public void toModelType_nullType_throwsIllegalValueException() {
        JsonAdaptedTimedEvent timedEvent = new JsonAdaptedTimedEvent(VALID_NAME, VALID_DESCRIPTION, VALID_TIME, null);
        String expectedMessage = String.format(JsonAdaptedTimedEvent.MISSING_FIELD_MESSAGE_FORMAT, "type");
        assertThrows(IllegalValueException.class, expectedMessage, timedEvent::toModelType);
    }
}
