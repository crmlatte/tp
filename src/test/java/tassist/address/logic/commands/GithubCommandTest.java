package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Github;
import tassist.address.model.person.Person;
import tassist.address.testutil.PersonBuilder;


/**
 * Contains integration tests (interaction with the Model) and unit tests for GithubCommand.
 */
public class GithubCommandTest {

    private static final String GITHUB_STUB = "https://github.com/githublink";
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validGithubUrl_success() throws Exception {
        Model model = new ModelManager();
        Person originalPerson = new PersonBuilder().withName("Alice").build();
        model.addPerson(originalPerson);

        Index validIndex = Index.fromZeroBased(0);
        Github validGithub = new Github("https://github.com/alice123");

        GithubCommand githubCommand = new GithubCommand(validIndex, validGithub);

        CommandResult result = githubCommand.execute(model);

        Person editedPerson = model.getFilteredPersonList().get(0);
        assertEquals(validGithub, editedPerson.getGithub()); // Assert GitHub was updated
        assertEquals(String.format(GithubCommand.MESSAGE_ADD_GITHUB_SUCCESS, Messages.format(editedPerson)),
                result.getFeedbackToUser()); // Assert success message
    }

    @Test
    public void execute_removeGithub_success() throws Exception {
        Model model = new ModelManager();
        Person originalPerson = new PersonBuilder().withName("Charlie")
                .withGithub("https://github.com/charlie").build();
        model.addPerson(originalPerson);

        Index validIndex = Index.fromZeroBased(0);
        Github emptyGithub = new Github(""); // Removing GitHub

        GithubCommand githubCommand = new GithubCommand(validIndex, emptyGithub);

        CommandResult result = githubCommand.execute(model);

        Person editedPerson = model.getFilteredPersonList().get(0);
        assertEquals("", editedPerson.getGithub().value); // Assert GitHub was removed
        assertEquals(String.format(GithubCommand.MESSAGE_DELETE_GITHUB_SUCCESS, Messages.format(editedPerson)),
                result.getFeedbackToUser()); // Assert success message
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        GithubCommand githubCommand = new GithubCommand(outOfBoundIndex, new Github(VALID_GITHUB_BOB));

        assertCommandFailure(githubCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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

        GithubCommand githubCommand = new GithubCommand(outOfBoundIndex, new Github(VALID_GITHUB_BOB));

        assertCommandFailure(githubCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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
