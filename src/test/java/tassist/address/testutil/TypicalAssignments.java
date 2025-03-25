package tassist.address.testutil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tassist.address.model.AddressBook;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.TimedEvent;

/**
 * A utility class containing a list of {@code Person} and {@code TimedEvent} objects to be used in tests.
 */
public class TypicalAssignments {
    public static final TimedEvent ASSIGNMENT_1 = new Assignment(
            "CS2103T Project",
            "Complete the project implementation",
            LocalDateTime.now().plusDays(7));

    public static final TimedEvent ASSIGNMENT_2 = new Assignment(
            "CS2101 Presentation",
            "Prepare for the final presentation",
            LocalDateTime.now().plusDays(14));

    public static final Person ALICE = new PersonBuilder().withName("Alice Pauline")
            .withEmail("alice@example.com")
            .withPhone("94351253")
            .withClassNumber("T01")
            .withStudentId("A1111111B")
            .withTags("friends")
            .withProgress("0")
            .build();

    public static final Person BENSON = new PersonBuilder().withName("Benson Meier")
            .withEmail("johnd@example.com")
            .withPhone("98765432")
            .withStudentId("A0101011A")
            .withClassNumber("T01")
            .withTags("owesMoney", "friends")
            .withProgress("30")
            .build();

    public static final Person CARL = new PersonBuilder().withName("Carl Kurz")
            .withPhone("95352563")
            .withEmail("heinz@example.com")
            .withStudentId("A0101010C")
            .withClassNumber("T02")
            .withProgress("50")
            .build();

    public static final Assignment ASSIGNMENT_A = new Assignment("CS2103T Project", "",
            LocalDateTime.of(2025, 1, 30, 23, 59));
    public static final Assignment ASSIGNMENT_B = new Assignment("CS2100 Assignment", "",
            LocalDateTime.of(2025, 2, 15, 23, 59));
    public static final Assignment ASSIGNMENT_C = new Assignment("CS2101 Presentation", "",
            LocalDateTime.of(2025, 3, 1, 23, 59));

    private TypicalAssignments() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical persons and timed events.
     */
    public static ReadOnlyAddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        for (TimedEvent timedEvent : getTypicalTimedEvents()) {
            ab.addTimedEvent(timedEvent);
        }
        return ab;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL));
    }

    public static List<TimedEvent> getTypicalTimedEvents() {
        return new ArrayList<>(Arrays.asList(ASSIGNMENT_1, ASSIGNMENT_2));
    }
}
