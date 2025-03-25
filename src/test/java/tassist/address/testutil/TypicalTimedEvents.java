package tassist.address.testutil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tassist.address.model.AddressBook;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.TimedEvent;

/**
 * A utility class containing a list of {@code TimedEvent} objects to be used in tests.
 */
public class TypicalTimedEvents {
    public static final TimedEvent ASSIGNMENT_1 = new Assignment(
            "CS2103T Project",
            "Complete the project implementation",
            LocalDateTime.now().plusDays(7));

    public static final TimedEvent ASSIGNMENT_2 = new Assignment(
            "CS2101 Presentation",
            "Prepare for the final presentation",
            LocalDateTime.now().plusDays(14));

    public static final TimedEvent ASSIGNMENT_3 = new Assignment(
            "CS2100 Lab",
            "Complete the lab exercises",
            LocalDateTime.now().plusDays(3));

    public static final TimedEvent ASSIGNMENT_4 = new Assignment(
            "CS2102 Database",
            "Submit the database design document",
            LocalDateTime.now().plusDays(5));

    public static final TimedEvent ASSIGNMENT_5 = new Assignment(
            "CS2106 OS",
            "Complete the operating systems assignment",
            LocalDateTime.now().plusDays(10));

    private TypicalTimedEvents() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical timed events.
     */
    public static ReadOnlyAddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (TimedEvent timedEvent : getTypicalTimedEvents()) {
            ab.addTimedEvent(timedEvent);
        }
        return ab;
    }

    public static List<TimedEvent> getTypicalTimedEvents() {
        return new ArrayList<>(Arrays.asList(ASSIGNMENT_1, ASSIGNMENT_2, ASSIGNMENT_3, ASSIGNMENT_4, ASSIGNMENT_5));
    }
} 