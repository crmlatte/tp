package tassist.address.testutil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tassist.address.model.AddressBook;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.TimedEvent;

/**
 * A utility class containing a list of {@code Person} and {@code TimedEvent} objects to be used in tests.
 * This class ensures that each test starts with a clean state where no timed events are assigned to students.
 */
public class TimedEventTestUtil {
    public static final TimedEvent ASSIGNMENT_1 = new Assignment(
            "CS2103T Project",
            "Complete the project implementation",
            LocalDateTime.of(2027, 4, 1, 23, 59));

    public static final TimedEvent ASSIGNMENT_2 = new Assignment(
            "CS2101 Presentation",
            "Prepare for the final presentation",
            LocalDateTime.of(2027, 4, 15, 23, 59));

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
            .withEmail("heinz@example.com")
            .withPhone("95352563")
            .withStudentId("A1234567A")
            .withClassNumber("T02")
            .withTags()
            .withProgress("0")
            .build();

    public static final List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL));
    }

    public static final List<TimedEvent> getTypicalTimedEvents() {
        return new ArrayList<>(Arrays.asList(ASSIGNMENT_1, ASSIGNMENT_2));
    }

    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Person person : getTypicalPersons()) {
            ab.addPerson(person);
        }
        for (TimedEvent timedEvent : getTypicalTimedEvents()) {
            ab.addTimedEvent(timedEvent);
        }
        return ab;
    }
}
