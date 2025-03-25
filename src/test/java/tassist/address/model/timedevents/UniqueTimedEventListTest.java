package tassist.address.model.timedevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalTimedEvents.ASSIGNMENT_1;
import static tassist.address.testutil.TypicalTimedEvents.ASSIGNMENT_2;
import static tassist.address.testutil.TypicalTimedEvents.ASSIGNMENT_3;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import tassist.address.model.timedevents.exceptions.DuplicateTimedEventException;
import tassist.address.model.timedevents.exceptions.TimedEventNotFoundException;
import tassist.address.testutil.TypicalTimedEvents;

public class UniqueTimedEventListTest {

    private final UniqueTimedEventList uniqueTimedEventList = new UniqueTimedEventList();

    @Test
    public void contains_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.contains(null));
    }

    @Test
    public void contains_timedEventNotInList_returnsFalse() {
        assertFalse(uniqueTimedEventList.contains(ASSIGNMENT_1));
    }

    @Test
    public void contains_timedEventInList_returnsTrue() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        assertTrue(uniqueTimedEventList.contains(ASSIGNMENT_1));
    }

    @Test
    public void contains_timedEventWithSameIdentityFieldsInList_returnsTrue() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        TimedEvent editedAssignment = new Assignment(
                ASSIGNMENT_1.getName(),
                "Different description",
                ASSIGNMENT_1.getTime());
        assertTrue(uniqueTimedEventList.contains(editedAssignment));
    }

    @Test
    public void add_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.add(null));
    }

    @Test
    public void add_duplicateTimedEvent_throwsDuplicateTimedEventException() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        assertThrows(DuplicateTimedEventException.class, () -> uniqueTimedEventList.add(ASSIGNMENT_1));
    }

    @Test
    public void setTimedEvent_nullTargetTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.setTimedEvent(null, ASSIGNMENT_1));
    }

    @Test
    public void setTimedEvent_nullEditedTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, null));
    }

    @Test
    public void setTimedEvent_targetTimedEventNotInList_throwsTimedEventNotFoundException() {
        assertThrows(TimedEventNotFoundException.class, () -> uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, ASSIGNMENT_1));
    }

    @Test
    public void setTimedEvent_editedTimedEventIsSameTimedEvent_success() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, ASSIGNMENT_1);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        expectedUniqueTimedEventList.add(ASSIGNMENT_1);
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvent_editedTimedEventHasSameIdentity_success() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        TimedEvent editedAssignment = new Assignment(
                ASSIGNMENT_1.getName(),
                "Different description",
                ASSIGNMENT_1.getTime());
        uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, editedAssignment);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        expectedUniqueTimedEventList.add(editedAssignment);
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvent_editedTimedEventHasDifferentIdentity_success() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, ASSIGNMENT_2);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        expectedUniqueTimedEventList.add(ASSIGNMENT_2);
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvent_editedTimedEventHasNonUniqueIdentity_throwsDuplicateTimedEventException() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        uniqueTimedEventList.add(ASSIGNMENT_2);
        assertThrows(DuplicateTimedEventException.class, () -> uniqueTimedEventList.setTimedEvent(ASSIGNMENT_1, ASSIGNMENT_2));
    }

    @Test
    public void remove_nullTimedEvent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.remove(null));
    }

    @Test
    public void remove_timedEventDoesNotExist_throwsTimedEventNotFoundException() {
        assertThrows(TimedEventNotFoundException.class, () -> uniqueTimedEventList.remove(ASSIGNMENT_1));
    }

    @Test
    public void remove_existingTimedEvent_removesTimedEvent() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        uniqueTimedEventList.remove(ASSIGNMENT_1);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvents_nullUniqueTimedEventList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.setTimedEvents((UniqueTimedEventList) null));
    }

    @Test
    public void setTimedEvents_uniqueTimedEventList_replacesOwnListWithProvidedUniqueTimedEventList() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        expectedUniqueTimedEventList.add(ASSIGNMENT_2);
        uniqueTimedEventList.setTimedEvents(expectedUniqueTimedEventList);
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvents_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTimedEventList.setTimedEvents((List<TimedEvent>) null));
    }

    @Test
    public void setTimedEvents_list_replacesOwnListWithProvidedList() {
        uniqueTimedEventList.add(ASSIGNMENT_1);
        List<TimedEvent> timedEventList = Collections.singletonList(ASSIGNMENT_2);
        uniqueTimedEventList.setTimedEvents(timedEventList);
        UniqueTimedEventList expectedUniqueTimedEventList = new UniqueTimedEventList();
        expectedUniqueTimedEventList.add(ASSIGNMENT_2);
        assertEquals(expectedUniqueTimedEventList, uniqueTimedEventList);
    }

    @Test
    public void setTimedEvents_listWithDuplicateTimedEvents_throwsDuplicateTimedEventException() {
        List<TimedEvent> listWithDuplicateTimedEvents = Arrays.asList(ASSIGNMENT_1, ASSIGNMENT_1);
        assertThrows(DuplicateTimedEventException.class, () -> uniqueTimedEventList.setTimedEvents(listWithDuplicateTimedEvents));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueTimedEventList.asUnmodifiableObservableList().remove(0));
    }
} 