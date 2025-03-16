package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Person;

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
}
