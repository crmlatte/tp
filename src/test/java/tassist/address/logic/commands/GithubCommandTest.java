package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_GITHUB_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.GithubCommand.MESSAGE_DUPLICATE_GITHUB;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Github;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;
import tassist.address.testutil.PersonBuilder;


/**
 * Contains integration tests (interaction with the Model) and unit tests for GithubCommand.
 */
public class GithubCommandTest {

    private static final String GITHUB_STUB = "https://github.com/githublink";
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void validGithubUrl_success_studentId() throws Exception {
        Model model = new ModelManager();

        Person originalPerson = new PersonBuilder().withName("Alice").withStudentId("A1239878D").build();
        model.addPerson(originalPerson);

        StudentId validStudentId = new StudentId("A1239878D");
        Github validGithub = new Github("https://github.com/alice123");

        GithubCommand githubCommand = new GithubCommand(validStudentId, validGithub);

        CommandResult result = githubCommand.execute(model);

        Person editedPerson = model.getFilteredPersonList().stream().filter(
                person -> person.getStudentId().equals(validStudentId)).findFirst().get();

        assertEquals(validGithub, editedPerson.getGithub()); // Assert GitHub was updated
        assertEquals(String.format(GithubCommand.MESSAGE_ADD_GITHUB_SUCCESS, Messages.format(editedPerson)),
                result.getFeedbackToUser()); // Assert success message
    }

    @Test
    public void validGithubUrl_success_index() throws Exception {
        Model model = new ModelManager();

        Person originalPerson = new PersonBuilder().withName("Alice").withStudentId("A1239878D").build();
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
    public void execute_invalidPersonStudentIdUnfilteredList_failure() {
        StudentId invalidStudentId = new StudentId("A9999999A");
        GithubCommand githubCommand = new GithubCommand(invalidStudentId, new Github(VALID_GITHUB_BOB));

        assertCommandFailure(githubCommand, model,
                Messages.MESSAGE_PERSON_NOT_FOUND + invalidStudentId);
    }

    @Test
    public void checkDuplicates_duplicateGithub_throwsCommandException() {
        // Setup model with two persons
        Person existingPerson = new PersonBuilder()
                .withName("Alice")
                .withStudentId("A1112222B")
                .withGithub("https://github.com/duplicateUser").build();
        Person targetPerson = new PersonBuilder()
                .withName("Bob")
                .withStudentId("A2221111B")
                .withGithub("https://github.com/originalUser").build();

        Model model = new ModelManager();
        model.addPerson(existingPerson);
        model.addPerson(targetPerson);

        Github duplicateGithub = new Github("https://github.com/duplicateUser");
        GithubCommand command = new GithubCommand(targetPerson.getStudentId(), duplicateGithub);

        assertCommandFailure(command, model, MESSAGE_DUPLICATE_GITHUB);
    }

    @Test
    public void checkDuplicate_github_success() throws Exception {
        Person alice = new PersonBuilder()
                .withName("Alice")
                .withStudentId("A1112222B")
                .withGithub("https://github.com/alice").build();
        Person bob = new PersonBuilder()
                .withName("Bob")
                .withStudentId("A2221111B")
                .withGithub("https://github.com/bob").build();

        Model model = new ModelManager();
        model.addPerson(alice);
        model.addPerson(bob);

        Github newGithub = new Github("https://github.com/charles");
        GithubCommand command = new GithubCommand(bob.getStudentId(), newGithub);
        CommandResult result = command.execute(model);
        Person editedBob = model.getFilteredPersonList().stream()
                .filter(p -> p.getStudentId().equals(bob.getStudentId())).findFirst().get();
        assertEquals(newGithub, editedBob.getGithub()); // Assert GitHub was updated
        assertEquals(String.format(GithubCommand.MESSAGE_ADD_GITHUB_SUCCESS, Messages.format(editedBob)),
                result.getFeedbackToUser());
    }

    @Test
    public void equals() {
        final GithubCommand standardCommand = new GithubCommand(
                new StudentId(VALID_STUDENTID_AMY), new Github(VALID_GITHUB_AMY));

        // same values -> returns true
        GithubCommand commandWithSameValues = new GithubCommand(
                new StudentId(VALID_STUDENTID_AMY), new Github(VALID_GITHUB_AMY));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new GithubCommand(
                new StudentId(VALID_STUDENTID_BOB), new Github(VALID_GITHUB_AMY))));

        // different github -> returns false
        assertFalse(standardCommand.equals(
                new GithubCommand(new StudentId(VALID_STUDENTID_AMY), new Github(VALID_GITHUB_BOB))));
    }
}
