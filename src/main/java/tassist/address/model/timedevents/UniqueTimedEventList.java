package tassist.address.model.timedevents;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tassist.address.model.timedevents.exceptions.DuplicateTimedEventException;
import tassist.address.model.timedevents.exceptions.TimedEventNotFoundException;

/**
 * A list of timed events that enforces uniqueness between its elements and does not allow nulls.
 * A timed event is considered unique by comparing using {@code TimedEvent#isSameTimedEvent(TimedEvent)}.
 * As such, adding and updating of timed events uses TimedEvent#isSameTimedEvent(TimedEvent) for equality
 * so as to ensure that the timed event with exactly the same fields (name and time) cannot exist in the list.
 *
 * Supports a minimal set of list operations.
 *
 * @see TimedEvent#isSameTimedEvent(TimedEvent)
 */
public class UniqueTimedEventList implements Iterable<TimedEvent> {

    private final ObservableList<TimedEvent> internalList = FXCollections.observableArrayList();
    private final ObservableList<TimedEvent> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent timed event as the given argument.
     */
    public boolean contains(TimedEvent toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameTimedEvent);
    }

    /**
     * Adds a timed event to the list.
     * The timed event must not already exist in the list.
     */
    public void add(TimedEvent toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateTimedEventException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the timed event {@code target} in the list with {@code editedTimedEvent}.
     * {@code target} must exist in the list.
     * The timed event identity of {@code editedTimedEvent} must not be the same as another existing timed event in the list.
     */
    public void setTimedEvent(TimedEvent target, TimedEvent editedTimedEvent) {
        requireNonNull(editedTimedEvent);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new TimedEventNotFoundException();
        }

        if (!target.isSameTimedEvent(editedTimedEvent) && contains(editedTimedEvent)) {
            throw new DuplicateTimedEventException();
        }

        internalList.set(index, editedTimedEvent);
    }

    /**
     * Removes the equivalent timed event from the list.
     * The timed event must exist in the list.
     */
    public void remove(TimedEvent toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new TimedEventNotFoundException();
        }
    }

    public void setTimedEvents(UniqueTimedEventList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code timedEvents}.
     * {@code timedEvents} must not contain duplicate timed events.
     */
    public void setTimedEvents(List<TimedEvent> timedEvents) {
        requireNonNull(timedEvents);
        if (!timedEventsAreUnique(timedEvents)) {
            throw new DuplicateTimedEventException();
        }
        internalList.setAll(timedEvents);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<TimedEvent> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<TimedEvent> iterator() {
        return internalUnmodifiableList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueTimedEventList)) {
            return false;
        }

        UniqueTimedEventList otherUniqueTimedEventList = (UniqueTimedEventList) other;
        return internalList.equals(otherUniqueTimedEventList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code timedEvents} contains only unique timed events.
     */
    private boolean timedEventsAreUnique(List<TimedEvent> timedEvents) {
        for (int i = 0; i < timedEvents.size() - 1; i++) {
            for (int j = i + 1; j < timedEvents.size(); j++) {
                if (timedEvents.get(i).isSameTimedEvent(timedEvents.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return internalList.toString();
    }
} 