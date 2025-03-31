package tassist.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import tassist.address.commons.util.ToStringBuilder;
import tassist.address.model.person.Person;
import tassist.address.model.person.UniquePersonList;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.UniqueTimedEventList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueTimedEventList timedEvents;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        timedEvents = new UniqueTimedEventList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the timed event list with {@code timedEvents}.
     * {@code timedEvents} must not contain duplicate timed events.
     */
    public void setTimedEvents(List<TimedEvent> timedEvents) {
        this.timedEvents.setTimedEvents(timedEvents);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setPersons(newData.getPersonList());
        setTimedEvents(newData.getTimedEventList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);
        persons.setPerson(target, editedPerson);
        // Verify data consistency
        assert hasPerson(editedPerson) : "Edited person should exist in address book";
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        // Verify person was removed
        assert !hasPerson(key) : "Person should be removed from address book";
    }

    //// timed event-level operations

    /**
     * Returns true if a timed event with the same identity as {@code timedEvent} exists.
     */
    public boolean hasTimedEvent(TimedEvent timedEvent) {
        requireNonNull(timedEvent);
        return timedEvents.contains(timedEvent);
    }

    /**
     * Adds a timed event to the address book.
     * The timed event must not already exist.
     */
    public void addTimedEvent(TimedEvent timedEvent) {
        timedEvents.add(timedEvent);
        // Verify timed event was added
        assert hasTimedEvent(timedEvent) : "Timed event should exist in address book";
    }

    /**
     * Removes the given timed event from the address book.
     * The timed event must exist in the address book.
     */
    public void removeTimedEvent(TimedEvent timedEvent) {
        requireNonNull(timedEvent);
        timedEvents.remove(timedEvent);
        // Verify timed event was removed
        assert !hasTimedEvent(timedEvent) : "Timed event should be removed from address book";
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("timedEvents", timedEvents)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<TimedEvent> getTimedEventList() {
        return timedEvents.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        return persons.equals(otherAddressBook.persons)
                && timedEvents.equals(otherAddressBook.timedEvents);
    }

    @Override
    public int hashCode() {
        return persons.hashCode() ^ timedEvents.hashCode();
    }
}
