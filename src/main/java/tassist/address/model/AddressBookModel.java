package tassist.address.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import tassist.address.commons.core.GuiSettings;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Represents the in-memory model of the address book data.
 */
public class AddressBookModel implements Model {
    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final FilteredList<TimedEvent> filteredTimedEvents;
    private final SortedList<TimedEvent> sortedTimedEvents;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public AddressBookModel(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireNonNull(addressBook);
        requireNonNull(userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons);
        filteredTimedEvents = new FilteredList<>(this.addressBook.getTimedEventList());
        sortedTimedEvents = new SortedList<>(filteredTimedEvents);
    }

    public AddressBookModel() {
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
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(target);
        requireNonNull(editedPerson);
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
    }

    @Override
    public void updateSortedPersonList(Comparator<Person> comparator) {
        requireNonNull(comparator);
        sortedPersons.setComparator(comparator);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof AddressBookModel)) {
            return false;
        }

        // state check
        AddressBookModel other = (AddressBookModel) obj;
        return addressBook.equals(other.addressBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPersons.equals(other.filteredPersons)
                && filteredTimedEvents.equals(other.filteredTimedEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressBook, userPrefs, filteredPersons, filteredTimedEvents);
    }
}
