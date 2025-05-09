package tassist.address.model;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import tassist.address.commons.core.GuiSettings;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    /** {@code Predicate} that always evaluate to true */
    Predicate<TimedEvent> PREDICATE_SHOW_ALL_TIMED_EVENTS = unused -> true;

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Sets the user prefs' address book file path.
     */
    void setAddressBookFilePath(Path addressBookFilePath);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Sort the student list by the given {@code comparator}.
     */
    void updateSortedPersonList(Comparator<Person> comparator);

    /**
     * Returns true if a timed event with the same identity as {@code timedEvent} exists.
     */
    boolean hasTimedEvent(TimedEvent timedEvent);

    /**
     * Adds the given timed event.
     * {@code timedEvent} must not already exist.
     */
    void addTimedEvent(TimedEvent timedEvent);

    /**
     * Deletes the given timed event.
     * The timed event must exist in the address book.
     */
    void deleteTimedEvent(TimedEvent timedEvent);

    /** Returns an unmodifiable view of the timed events list */
    ObservableList<TimedEvent> getTimedEventList();

    /** Returns an unmodifiable view of the filtered timed events list */
    ObservableList<TimedEvent> getFilteredTimedEventList();

    /**
     * Updates the filter of the filtered timed events list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredTimedEventList(Predicate<TimedEvent> predicate);

    /**
     * Sort the timed events list by the given {@code comparator}.
     */
    void updateSortedTimedEventList(Comparator<TimedEvent> comparator);
}
