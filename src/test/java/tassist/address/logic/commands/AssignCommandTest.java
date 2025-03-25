package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.AssignCommandTestUtil.getTypicalAddressBook;
import static tassist.address.testutil.AssignCommandTestUtil.ALICE;
import static tassist.address.testutil.AssignCommandTestUtil.BENSON;
import static tassist.address.testutil.AssignCommandTestUtil.CARL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.AddressBook;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.testutil.PersonBuilder;
import tassist.address.testutil.AssignCommandTestUtil;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code AssignCommand}.
 */
public class AssignCommandTest {

    private Model model;
    private Person alice;
    private Person benson;
    private Person carl;

    @BeforeEach
    public void setUp() {
        // Create fresh instances of the persons to avoid state sharing
        alice = new PersonBuilder(ALICE).build();
        benson = new PersonBuilder(BENSON).build();
        carl = new PersonBuilder(CARL).build();
        
        // Create a fresh address book with the new person instances
        AddressBook ab = new AddressBook();
        ab.addPerson(alice);
        ab.addPerson(benson);
        ab.addPerson(carl);
        
        // Add the timed events
        for (TimedEvent timedEvent : AssignCommandTestUtil.getTypicalTimedEvents()) {
            ab.addTimedEvent(timedEvent);
        }
        
        model = new ModelManager(ab, new UserPrefs());
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        String expectedMessage = String.format(AssignCommand.MESSAGE_ASSIGN_SUCCESS, Messages.format(targetPerson));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = new PersonBuilder(targetPerson).build();
        expectedPerson.addTimedEvent(expectedModel.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased()));
        expectedModel.setPerson(targetPerson, expectedPerson);

        assertCommandSuccess(assignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validStudentId_success() {
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, alice.getStudentId());

        String expectedMessage = String.format(AssignCommand.MESSAGE_ASSIGN_SUCCESS, Messages.format(alice));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = new PersonBuilder(alice).build();
        expectedPerson.addTimedEvent(expectedModel.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased()));
        expectedModel.setPerson(alice, expectedPerson);

        assertCommandSuccess(assignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validClassNumber_success() {
        ClassNumber classNumber = new ClassNumber("T01");
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, classNumber);

        // Create expected model with students in the class
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedAlice = new PersonBuilder(alice).build();
        Person expectedBenson = new PersonBuilder(benson).build();
        expectedAlice.addTimedEvent(expectedModel.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased()));
        expectedBenson.addTimedEvent(expectedModel.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased()));
        expectedModel.setPerson(alice, expectedAlice);
        expectedModel.setPerson(benson, expectedBenson);

        String expectedMessage = String.format(AssignCommand.MESSAGE_ASSIGN_SUCCESS, Messages.format(alice))
                + "\n" + String.format(AssignCommand.MESSAGE_ASSIGN_SUCCESS, Messages.format(benson));

        assertCommandSuccess(assignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidTimedEventIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getTimedEventList().size() + 1);
        AssignCommand assignCommand = new AssignCommand(outOfBoundIndex, INDEX_FIRST_PERSON);

        assertCommandFailure(assignCommand, model, Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, outOfBoundIndex);

        assertCommandFailure(assignCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidStudentId_failure() {
        StudentId nonExistentId = new StudentId("A9999999Z");
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, nonExistentId);

        assertCommandFailure(assignCommand, model, Messages.MESSAGE_PERSON_NOT_FOUND + nonExistentId);
    }

    @Test
    public void execute_invalidClassNumber_failure() {
        ClassNumber nonExistentClass = new ClassNumber("T99");
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_PERSON, nonExistentClass);

        assertCommandFailure(assignCommand, model, String.format(AssignCommand.MESSAGE_NO_STUDENTS_IN_CLASS, nonExistentClass));
    }

    @Test
    public void equals() {
        final AssignCommand standardCommand = new AssignCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);

        // same values -> returns true
        AssignCommand commandWithSameValues = new AssignCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different timed event index -> returns false
        assertFalse(standardCommand.equals(new AssignCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON)));

        // different student index -> returns false
        assertFalse(standardCommand.equals(new AssignCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));

        // different student ID -> returns false
        assertFalse(standardCommand.equals(new AssignCommand(INDEX_FIRST_PERSON, new StudentId("A1234567B"))));

        // different class number -> returns false
        assertFalse(standardCommand.equals(new AssignCommand(INDEX_FIRST_PERSON, new ClassNumber("T01"))));
    }
} 