package tassist.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.model.AddressBook;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_TIMED_EVENT = "Timed events list contains duplicate timed event(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedTimedEvent> timedEvents = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and timed events.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("timedEvents") List<JsonAdaptedTimedEvent> timedEvents) {
        this.persons.addAll(persons);
        if (timedEvents != null) {
            this.timedEvents.addAll(timedEvents);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        timedEvents.addAll(source.getTimedEventList().stream().map(JsonAdaptedTimedEvent::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        for (JsonAdaptedTimedEvent jsonAdaptedTimedEvent : timedEvents) {
            TimedEvent timedEvent = jsonAdaptedTimedEvent.toModelType();
            if (addressBook.hasTimedEvent(timedEvent)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_TIMED_EVENT);
            }
            addressBook.addTimedEvent(timedEvent);
        }
        return addressBook;
    }

}
