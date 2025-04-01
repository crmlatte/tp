package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.NONEXISTENT_STUDENTID;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.testutil.PersonBuilder;
import tassist.address.testutil.TimedEventBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ClassCommand.
 */
public class ClassCommandTest {

    private static final ClassNumber VALID_CLASS = new ClassNumber("T01");
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validClassNumber_success() throws CommandException {
        ClassCommand classCommand = new ClassCommand(INDEX_FIRST_PERSON, VALID_CLASS);

        ClassNumber validClassNumber = new ClassNumber("T01");
        CommandResult result = classCommand.execute(model);

        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(validClassNumber, editedPerson.getClassNumber()); // Assert tutorial was correctly assigned
        assertEquals(String.format(ClassCommand.MESSAGE_ADD_CLASS_SUCCESS, Messages.format(editedPerson)),
                result.getFeedbackToUser()); // Assert success message
    }

    @Test
    public void execute_validStudentId_success() throws CommandException {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ClassNumber newClass = new ClassNumber("T02");
        ClassCommand classCommand = new ClassCommand(targetPerson.getStudentId(), newClass);

        CommandResult result = classCommand.execute(model);
        Person edited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(newClass, edited.getClassNumber());
        assertEquals(String.format(ClassCommand.MESSAGE_ADD_CLASS_SUCCESS, Messages.format(edited)),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_studentIdNotFound_throwsCommandException() {
        StudentId nonExistentStudentId = new StudentId(NONEXISTENT_STUDENTID);
        ClassNumber classNumber = new ClassNumber("T01");

        ClassCommand command = new ClassCommand(nonExistentStudentId, classNumber);

        CommandException thrown = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(Messages.MESSAGE_PERSON_NOT_FOUND + "A9999999Z", thrown.getMessage());
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ClassCommand remarkCommand = new ClassCommand(outOfBoundIndex, new ClassNumber(VALID_CLASS_BOB));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        ClassCommand remarkCommand = new ClassCommand(outOfBoundIndex, new ClassNumber(VALID_CLASS_BOB));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final ClassCommand standardCommand = new ClassCommand(INDEX_FIRST_PERSON,
                new ClassNumber(VALID_CLASS_AMY));

        // same values -> returns true
        ClassCommand commandWithSameValues = new ClassCommand(INDEX_FIRST_PERSON,
                new ClassNumber(VALID_CLASS_AMY));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new ClassCommand(INDEX_SECOND_PERSON,
                new ClassNumber(VALID_CLASS_AMY))));

        // different remark -> returns false
        assertFalse(standardCommand.equals(new ClassCommand(INDEX_FIRST_PERSON,
                new ClassNumber(VALID_CLASS_BOB))));


    }

    @Test
    public void equals_withStudentId() {
        final StudentId studentIdAmy = new StudentId(VALID_STUDENTID_AMY);
        final StudentId studentIdBob = new StudentId(VALID_STUDENTID_BOB);
        final ClassNumber classAmy = new ClassNumber(VALID_CLASS_AMY);
        final ClassNumber classBob = new ClassNumber(VALID_CLASS_BOB);

        final ClassCommand standardCommand = new ClassCommand(studentIdAmy, classAmy);

        // same values -> returns true
        ClassCommand commandWithSameValues = new ClassCommand(studentIdAmy, classAmy);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different studentId -> returns false
        assertFalse(standardCommand.equals(new ClassCommand(studentIdBob, classAmy)));

        // different classNumber -> returns false
        assertFalse(standardCommand.equals(new ClassCommand(studentIdAmy, classBob)));
    }

    @Test
    public void execute_preservesAssignments_success() throws Exception {
        Model model = new ModelManager();

        // Create a person with an assignment
        Person originalPerson = new PersonBuilder()
                .withName("Alice")
                .withStudentId("A1239878D")
                .build();
        model.addPerson(originalPerson);

        // Add an assignment to the person
        TimedEvent assignment = new TimedEventBuilder()
                .withName("Test Assignment")
                .withDescription("Test Description")
                .withTime(LocalDateTime.now().plusDays(7))
                .build();
        originalPerson.addTimedEvent(assignment);

        // Update class
        StudentId validStudentId = new StudentId("A1239878D");
        ClassNumber validClass = new ClassNumber("T01");
        ClassCommand classCommand = new ClassCommand(validStudentId, validClass);
        CommandResult result = classCommand.execute(model);

        // Get the edited person
        Person editedPerson = model.getFilteredPersonList().stream()
                .filter(person -> person.getStudentId().equals(validStudentId))
                .findFirst()
                .get();

        // Verify class was updated
        assertEquals(validClass, editedPerson.getClassNumber());

        // Verify assignment was preserved
        List<TimedEvent> assignments = editedPerson.getTimedEventsList().asUnmodifiableObservableList();
        assertEquals(1, assignments.size());
        assertEquals(assignment, assignments.get(0));

        // Verify success message
        assertEquals(String.format(ClassCommand.MESSAGE_ADD_CLASS_SUCCESS, Messages.format(editedPerson)),
                result.getFeedbackToUser());
    }
}
