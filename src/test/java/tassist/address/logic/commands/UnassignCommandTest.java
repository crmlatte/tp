package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.testutil.AssignCommandTestUtil.ALICE;
import static tassist.address.testutil.AssignCommandTestUtil.BENSON;
import static tassist.address.testutil.AssignCommandTestUtil.CARL;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.AddressBook;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.testutil.AssignCommandTestUtil;
import tassist.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code UnassignCommand}.
 */
public class UnassignCommandTest {

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
    public void execute_validIndexUnfilteredList_showsConfirmationMessage() throws CommandException {
        TimedEvent eventToUnassign = model.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_PERSON);

        // Expected confirmation message
        String expectedMessage = String.format(UnassignCommand.MESSAGE_CONFIRM, eventToUnassign.getName());

        // Ensure confirmation message is shown first
        CommandResult commandResult = unassignCommand.execute(model);
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
        assertTrue(commandResult.requiresConfirmation());
    }

    @Test
    public void executeConfirmed_validIndexUnfilteredList_unassignsSuccessfully() throws CommandException {
        TimedEvent eventToUnassign = model.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        CommandResult confirmationResult = unassignCommand.execute(model);
        assertEquals(String.format(UnassignCommand.MESSAGE_CONFIRM, eventToUnassign.getName()),
                confirmationResult.getFeedbackToUser());

        // Step 2: Confirm unassignment
        String expectedMessage = String.format(UnassignCommand.MESSAGE_UNASSIGN_EVENT_SUCCESS,
                eventToUnassign.getName());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteTimedEvent(eventToUnassign);

        CommandResult unassignmentResult = unassignCommand.executeConfirmed(model);
        assertEquals(expectedMessage, unassignmentResult.getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getTimedEventList().size() + 1);
        UnassignCommand unassignCommand = new UnassignCommand(outOfBoundIndex);

        assertCommandFailure(unassignCommand, model, Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX);
    }

    @Test
    public void executeConfirmed_invalidIndex_returnsInvalidTimedEventDisplayedIndexMessage() throws CommandException {
        Index outOfBoundIndex = Index.fromOneBased(model.getTimedEventList().size() + 1);
        UnassignCommand unassignCommand = new UnassignCommand(outOfBoundIndex);

        CommandResult result = unassignCommand.executeConfirmed(model);
        assertEquals(Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX, result.getFeedbackToUser());
    }

    @Test
    public void getConfirmationMessage_showsCorrectMessage() throws CommandException {
        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_PERSON);
        TimedEvent eventToUnassign = model.getTimedEventList().get(INDEX_FIRST_PERSON.getZeroBased());

        // First execute to set up the model
        unassignCommand.execute(model);

        String confirmationMessage = unassignCommand.getConfirmationMessage();
        assertEquals(String.format(UnassignCommand.MESSAGE_CONFIRM, eventToUnassign.getName()), confirmationMessage);
    }

    @Test
    public void execute_invalidConfirmationInput_returnsErrorMessage() throws CommandException {
        UnassignCommand unassignCommand = new UnassignCommand(INDEX_FIRST_PERSON);

        // Step 1: First execution should return confirmation message
        unassignCommand.execute(model);

        // Step 2: Simulate invalid response
        CommandResult commandResult = new CommandResult("Invalid response. Please enter Y/N.");
        assertEquals(commandResult.getFeedbackToUser(), "Invalid response. Please enter Y/N.");
    }

    @Test
    public void equals() {
        final UnassignCommand standardCommand = new UnassignCommand(INDEX_FIRST_PERSON);

        // same values -> returns true
        UnassignCommand commandWithSameValues = new UnassignCommand(INDEX_FIRST_PERSON);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new UnassignCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toString_returnsExpectedString() {
        UnassignCommand command = new UnassignCommand(INDEX_FIRST_PERSON);
        assertEquals(String.format("UnassignCommand{targetIndex=%s}", INDEX_FIRST_PERSON),
                command.toString());
    }
}
