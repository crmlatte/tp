package tassist.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalPersons.ALICE;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tassist.address.model.person.Person;
import tassist.address.model.person.exceptions.DuplicatePersonException;
import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.exceptions.DuplicateTimedEventException;
import tassist.address.model.timedevents.exceptions.TimedEventNotFoundException;
import tassist.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void getTimedEventList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getTimedEventList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{"
                + "persons=" + addressBook.getPersonList()
                + ", timedEvents=" + addressBook.getTimedEventList()
                + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void hasTimedEvent_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasTimedEvent(null));
    }

    @Test
    public void hasTimedEvent_timedEventNotInAddressBook_returnsFalse() {
        TimedEvent timedEvent = new Assignment("Test Assignment", "Test Description", LocalDateTime.now());
        assertFalse(addressBook.hasTimedEvent(timedEvent));
    }

    @Test
    public void hasTimedEvent_timedEventInAddressBook_returnsTrue() {
        TimedEvent timedEvent = new Assignment("Test Assignment", "Test Description", LocalDateTime.now());
        addressBook.addTimedEvent(timedEvent);
        assertTrue(addressBook.hasTimedEvent(timedEvent));
    }

    @Test
    public void addTimedEvent_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addTimedEvent(null));
    }

    @Test
    public void addTimedEvent_duplicateTimedEvent_throwsDuplicateTimedEventException() {
        TimedEvent timedEvent = new Assignment("Test Assignment", "Test Description", LocalDateTime.now());
        addressBook.addTimedEvent(timedEvent);
        assertThrows(DuplicateTimedEventException.class, () -> addressBook.addTimedEvent(timedEvent));
    }

    @Test
    public void removeTimedEvent_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.removeTimedEvent(null));
    }

    @Test
    public void removeTimedEvent_timedEventDoesNotExist_throwsTimedEventNotFoundException() {
        TimedEvent timedEvent = new Assignment("Test Assignment", "Test Description", LocalDateTime.now());
        assertThrows(TimedEventNotFoundException.class, () -> addressBook.removeTimedEvent(timedEvent));
    }

    @Test
    public void removeTimedEvent_existingTimedEvent_removesTimedEvent() {
        TimedEvent timedEvent = new Assignment("Test Assignment", "Test Description", LocalDateTime.now());
        addressBook.addTimedEvent(timedEvent);
        addressBook.removeTimedEvent(timedEvent);
        assertFalse(addressBook.hasTimedEvent(timedEvent));
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<TimedEvent> timedEvents = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<TimedEvent> getTimedEventList() {
            return timedEvents;
        }
    }

}
