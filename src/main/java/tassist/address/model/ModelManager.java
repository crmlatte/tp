package tassist.address.model;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import tassist.address.commons.core.GuiSettings;
import tassist.address.commons.core.LogsCenter;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final FilteredList<TimedEvent> filteredTimedEvents;
    private final SortedList<TimedEvent> sortedTimedEvents;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons);
        filteredTimedEvents = new FilteredList<>(this.addressBook.getTimedEventList());
        sortedTimedEvents = new SortedList<>(filteredTimedEvents);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
        // Verify person was deleted
        assert !hasPerson(target) : "Person should be removed from address book";
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        // Verify person was added and visible in filtered list
        assert hasPerson(person) : "Person should exist in address book";
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);
        addressBook.setPerson(target, editedPerson);
    }

    @Override
    public boolean hasTimedEvent(TimedEvent timedEvent) {
        requireNonNull(timedEvent);
        return addressBook.hasTimedEvent(timedEvent);
    }

    @Override
    public void addTimedEvent(TimedEvent timedEvent) {
        addressBook.addTimedEvent(timedEvent);
        updateFilteredTimedEventList(PREDICATE_SHOW_ALL_TIMED_EVENTS);
    }

    @Override
    public void deleteTimedEvent(TimedEvent timedEvent) {
        addressBook.removeTimedEvent(timedEvent);
    }

    @Override
    public ObservableList<TimedEvent> getTimedEventList() {
        return addressBook.getTimedEventList();
    }

    @Override
    public ObservableList<TimedEvent> getFilteredTimedEventList() {
        return sortedTimedEvents;
    }

    @Override
    public void updateFilteredTimedEventList(Predicate<TimedEvent> predicate) {
        requireNonNull(predicate);
        filteredTimedEvents.setPredicate(predicate);
    }

    @Override
    public void updateSortedTimedEventList(Comparator<TimedEvent> comparator) {
        requireNonNull(comparator);
        sortedTimedEvents.setComparator(comparator);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
        // Verify filtered list state
        assert filteredPersons.stream().allMatch(predicate) : "All filtered persons should match predicate";
    }

    @Override
    public void updateSortedPersonList(Comparator<Person> comparator) {
        requireNonNull(comparator);
        sortedPersons.setComparator(comparator);
        // Verify sorted list state
        assert sortedPersons.getComparator() == comparator : "Sorted list should use the provided comparator";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredTimedEvents.equals(otherModelManager.filteredTimedEvents);
    }
}
