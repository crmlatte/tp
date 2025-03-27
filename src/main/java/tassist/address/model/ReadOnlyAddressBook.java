package tassist.address.model;

import javafx.collections.ObservableList;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the timed events list.
     * This list will not contain any duplicate timed events.
     */
    ObservableList<TimedEvent> getTimedEventList();

}
