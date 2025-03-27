package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.timedevents.Assignment;
import tassist.address.testutil.TypicalAssignments;

public class AssignmentCommandTest {

    private Model model = new ModelManager(TypicalAssignments.getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullAssignment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignmentCommand(null));
    }

    @Test
    public void execute_assignmentAcceptedByModel_addSuccessful() throws Exception {
        Assignment validAssignment = new Assignment(
                "CS2103T Quiz",
                "Complete the quiz",
                LocalDateTime.now().plusDays(7));

        CommandResult commandResult = new AssignmentCommand(validAssignment).execute(model);

        assertEquals(
                String.format(AssignmentCommand.MESSAGE_SUCCESS, validAssignment.toString()),
                commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_duplicateAssignment_throwsCommandException() {
        Assignment assignmentInList = (Assignment) TypicalAssignments.ASSIGNMENT_1;
        AssignmentCommand assignmentCommand = new AssignmentCommand(assignmentInList);

        assertCommandFailure(assignmentCommand, model, AssignmentCommand.MESSAGE_DUPLICATE_ASSIGNMENT);
    }

    @Test
    public void equals() {
        Assignment assignment = new Assignment(
                "CS2103T Quiz",
                "Complete the quiz",
                LocalDateTime.now().plusDays(7));
        AssignmentCommand assignmentCommand = new AssignmentCommand(assignment);

        // same values -> returns true
        AssignmentCommand commandWithSameValues = new AssignmentCommand(assignment);
        assertTrue(assignmentCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(assignmentCommand.equals(assignmentCommand));

        // null -> returns false
        assertFalse(assignmentCommand.equals(null));

        // different types -> returns false
        assertFalse(assignmentCommand.equals(new ClearCommand()));

        // different assignment -> returns false
        Assignment differentAssignment = new Assignment(
                "Different Quiz",
                "Complete the different quiz",
                LocalDateTime.now().plusDays(7));
        assertFalse(assignmentCommand.equals(new AssignmentCommand(differentAssignment)));
    }
}
