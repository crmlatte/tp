package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.timedevents.Assignment;
import tassist.address.testutil.TypicalAssignments;
import tassist.address.testutil.TypicalPersons;

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

    @Test
    public void execute_assignOverdueAssignment_throwsCommandException() {
        Assignment overdueEvent = new Assignment("Quiz", "", LocalDateTime.now().minusDays(2));
        model.addTimedEvent(overdueEvent);
        model.addPerson(TypicalPersons.AMY);

        AssignCommand command = new AssignCommand(Index.fromOneBased(3), Index.fromOneBased(1));

        String expectedMessage = String.format(AssignCommand.MESSAGE_ASSIGN_FAILED_OVERDUE_ASSIGNMENT, "Quiz");
        CommandException thrown = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(expectedMessage, thrown.getMessage());
    }
}
