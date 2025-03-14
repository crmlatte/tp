package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_showsConfirmationMessage() throws CommandException {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Expected confirmation message
        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete));

        // Ensure confirmation message is shown first
        CommandResult commandResult = deleteCommand.execute(model);
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
        assertTrue(commandResult.requiresConfirmation());
    }

    @Test
    public void executeConfirmed_validIndexUnfilteredList_deletesSuccessfully() throws CommandException {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        CommandResult confirmationResult = deleteCommand.execute(model);
        assertEquals(String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)), confirmationResult.getFeedbackToUser());

        // Step 2: Confirm deletion
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete));
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        CommandResult deletionResult = deleteCommand.executeConfirmed(model);

        // Fix: Use assertEquals instead
        assertEquals(expectedMessage, deletionResult.getFeedbackToUser());
    }


    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void executeConfirmed_validIndexFilteredList_deletesSuccessfully() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        CommandResult confirmationResult = deleteCommand.execute(model);
        assertEquals(String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)), confirmationResult.getFeedbackToUser());

        // Step 2: Confirm deletion
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        CommandResult deletionResult = deleteCommand.executeConfirmed(model);

        // Fix: Use assertEquals instead
        assertEquals(expectedMessage, deletionResult.getFeedbackToUser());
    }


    @Test
    public void executeCancelled_showsCancellationMessage() throws CommandException {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        deleteCommand.execute(model);

        // Step 2: Cancel deletion
        CommandResult commandResult = new CommandResult(DeleteCommand.MESSAGE_DELETE_CANCELLED);
        assertEquals(commandResult.getFeedbackToUser(), DeleteCommand.MESSAGE_DELETE_CANCELLED);
    }

    @Test
    public void execute_invalidConfirmationInput_returnsErrorMessage() throws CommandException {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        deleteCommand.execute(model);

        // Step 2: Simulate invalid response
        CommandResult commandResult = new CommandResult("Invalid response. Please enter Y/N.");
        assertEquals(commandResult.getFeedbackToUser(), "Invalid response. Please enter Y/N.");
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertEquals(deleteFirstCommand, deleteFirstCommand);

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertEquals(deleteFirstCommand, deleteFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, deleteFirstCommand);

        // null -> returns false
        assertNotEquals(null, deleteFirstCommand);

        // different person -> returns false
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
