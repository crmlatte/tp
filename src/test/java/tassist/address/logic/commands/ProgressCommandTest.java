package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static tassist.address.logic.Messages.MESSAGE_PERSON_NOT_FOUND;
import static tassist.address.logic.commands.CommandTestUtil.NONEXISTENT_STUDENTID;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.ProgressCommand.MESSAGE_SET_PROGRESS_SUCCESS;
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
import tassist.address.model.person.Person;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;

public class ProgressCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_success() throws Exception {
        Progress newProgress = new Progress("20");
        ProgressCommand command = new ProgressCommand(INDEX_FIRST_PERSON, new Progress("20"));

        CommandResult result = command.execute(model);
        Person edited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(newProgress, edited.getProgress());
        assertEquals(String.format(MESSAGE_SET_PROGRESS_SUCCESS, Messages.format(edited)),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_validStudentId_success() throws Exception {
        Person targetPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Progress newProgress = new Progress("20");
        ProgressCommand command = new ProgressCommand(targetPerson.getStudentId(), new Progress("20"));

        CommandResult result = command.execute(model);
        Person edited = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertEquals(newProgress, edited.getProgress());
        assertEquals(String.format(MESSAGE_SET_PROGRESS_SUCCESS, Messages.format(edited)),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_nonexistentStudentId_throwsCommandException() {
        StudentId nonexistentId = new StudentId(NONEXISTENT_STUDENTID);
        ProgressCommand command = new ProgressCommand(nonexistentId, new Progress("20"));

        CommandException thrown = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(MESSAGE_PERSON_NOT_FOUND + nonexistentId, thrown.getMessage());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ProgressCommand command = new ProgressCommand(invalidIndex, new Progress("10"));

        assertCommandFailure(command, model, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        Progress progress80 = new Progress("80");
        Progress progress50 = new Progress("50");

        ProgressCommand byIndex1 = new ProgressCommand(INDEX_FIRST_PERSON, progress80);
        ProgressCommand byIndex2 = new ProgressCommand(INDEX_FIRST_PERSON, progress80);

        StudentId id = new StudentId("A0123456X");
        ProgressCommand byId = new ProgressCommand(id, progress80);
        ProgressCommand byIdCopy = new ProgressCommand(id, progress80);

        //by index
        assertTrue(byIndex1.equals(byIndex2));
        assertTrue(byIndex1.equals(byIndex1));
        //different progress
        assertFalse(byIndex1.equals(new ProgressCommand(INDEX_FIRST_PERSON, progress50)));
        //different index
        assertFalse(byIndex1.equals(new ProgressCommand(INDEX_SECOND_PERSON, progress80)));

        //by studentId
        assertTrue(byId.equals(byIdCopy));
        assertTrue(byId.equals(byId));
        //different progress
        assertFalse(byId.equals(new ProgressCommand(id, progress50)));
        //different studentId
        assertFalse(byId.equals(new ProgressCommand(new StudentId("A1111111Z"), progress80)));

        assertFalse(byIndex1.equals(byId));
        assertFalse(byIndex1.equals(null));
        assertFalse(byIndex1.equals(5));
    }
}
