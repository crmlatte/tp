package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.GithubCommand.MESSAGE_ARGUMENTS;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Github;

/**
 * Contains integration tests (interaction with the Model) and unit tests for GithubCommand.
 */
public class GithubCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute() {
        final Github github = new Github("Some github");

        assertCommandFailure(new GithubCommand(INDEX_FIRST_PERSON, github), model,
                String.format(MESSAGE_ARGUMENTS, INDEX_FIRST_PERSON.getOneBased(), github));
    }

    @Test
    public void equals() {
        final GithubCommand standardCommand = new GithubCommand(INDEX_FIRST_PERSON, new Github(VALID_GITHUB_AMY));

        // same values -> returns true
        GithubCommand commandWithSameValues = new GithubCommand(INDEX_FIRST_PERSON, new Github(VALID_GITHUB_AMY));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new GithubCommand(INDEX_SECOND_PERSON, new Github(VALID_GITHUB_AMY))));

        // different github -> returns false
        assertFalse(standardCommand.equals(new GithubCommand(INDEX_FIRST_PERSON, new Github(VALID_GITHUB_BOB))));
    }
}
