package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tassist.address.model.AddressBook;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.timedevents.Assignment;

public class ViewCommandTest {
    private Model model = new ModelManager(new AddressBook(), new UserPrefs());

    @Test
    public void execute_emptyTimedEventList_success() {
        ViewCommand viewCommand = new ViewCommand();
        CommandResult commandResult = viewCommand.execute(model);
        assertEquals(ViewCommand.MESSAGE_NO_EVENTS, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_nonEmptyTimedEventList_success() {
        // Create test assignments
        Assignment assignment1 = new Assignment("Test Assignment 1", "Description 1",
                LocalDateTime.now().plusDays(1));
        Assignment assignment2 = new Assignment("Test Assignment 2", "Description 2",
                LocalDateTime.now().plusDays(2));

        // Add assignments to model
        model.addTimedEvent(assignment1);
        model.addTimedEvent(assignment2);

        // Execute view command
        ViewCommand viewCommand = new ViewCommand();
        CommandResult commandResult = viewCommand.execute(model);

        // Verify the result
        String expectedMessage = ViewCommand.MESSAGE_SUCCESS + "\n"
                + "1. Test Assignment 1 - Assignment\n   Due: "
                + assignment1.getTime().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\n"
                + "2. Test Assignment 2 - Assignment\n   Due: "
                + assignment2.getTime().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\n";
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ViewCommand viewCommand = new ViewCommand();
        assertThrows(NullPointerException.class, () -> viewCommand.execute(null));
    }

    @Test
    public void equals() {
        ViewCommand viewCommand = new ViewCommand();
        ViewCommand viewCommandCopy = new ViewCommand();

        // same object -> returns true
        assertTrue(viewCommand.equals(viewCommand));

        // different types -> returns false
        assertFalse(viewCommand.equals(1));

        // null -> returns false
        assertFalse(viewCommand.equals(null));
    }

    @Test
    public void toString_returnsExpectedString() {
        ViewCommand command = new ViewCommand();
        assertEquals("ViewCommand{}", command.toString());
    }
}
