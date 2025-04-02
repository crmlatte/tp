package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tassist.address.commons.core.GuiSettings;
import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.ReadOnlyUserPrefs;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;
import tassist.address.model.person.Repository;
import tassist.address.model.person.StudentId;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.testutil.PersonBuilder;

public class RepoCommandTest {

    @Test
    public void constructor_invalidRepository_throwsException() {
        String invalidRepo = "https://github.com/invalid_user/valid-repo"; // underscore in username
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Repository(invalidRepo);
        });

        assertTrue(exception.getMessage().contains(Repository.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parser_validInputs_constructsValidRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", "valid-repo");
        assertEquals("https://github.com/ValidUser/valid-repo", repo.value);
    }

    @Test
    public void parser_invalidUsername_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("invalid_user!", "valid-repo"); // underscore is invalid in username
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_invalidRepositoryName_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", "-invalidRepo"); // starts with a dash
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_nullUsername_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo(null, "valid-repo");
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_nullRepositoryName_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", null);
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_bothNull_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo(null, null);
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_edgeCase_validRepoWithUnderscoreAndDot() {
        Repository repo = RepoCommand.createRepo("Group-4", "Wealth_Vault.v2");
        assertEquals("https://github.com/Group-4/Wealth_Vault.v2", repo.toString());
    }

    @Test
    public void constructor_emptyString_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Repository("");
        });

        assertTrue(exception.getMessage().contains("Repositories should be written in the format"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new Repository(null);
        });
    }

    @Test
    public void execute_validIndex_success() throws Exception {
        Person person = new PersonBuilder().withStudentId("A1234567Z").build();
        ModelStubWithPersonList model = new ModelStubWithPersonList(person);
        RepoCommand command = new RepoCommand(Index.fromZeroBased(0), "ValidUser", "cool-project", null);

        command.execute(model);

        Person updated = model.getLastEditedPerson();
        assertEquals("https://github.com/ValidUser/cool-project", updated.getRepository().toString());
    }

    @Test
    public void execute_validStudentId_success() throws Exception {
        Person person = new PersonBuilder().withStudentId("A7654321Z").build();
        ModelStubWithPersonList model = new ModelStubWithPersonList(person);
        RepoCommand command = new RepoCommand(new StudentId("A7654321Z"), "team42", "assignment", null);

        command.execute(model);

        Person updated = model.getLastEditedPerson();
        assertEquals("https://github.com/team42/assignment", updated.getRepository().toString());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Person person = new PersonBuilder().withStudentId("A1234567Z").build();
        ModelStubWithPersonList modelStub = new ModelStubWithPersonList(person);
        Index invalidIndex = Index.fromOneBased(999); // out of bounds
        RepoCommand command = new RepoCommand(invalidIndex, "user", "repo", null);

        assertThrows(CommandException.class, () -> command.execute(modelStub));
    }

    @Test
    public void execute_invalidStudentId_throwsCommandException() {
        Person person = new PersonBuilder().withStudentId("A7654321Z").build();
        ModelStubWithPersonList modelStub = new ModelStubWithPersonList(person);
        StudentId invalidStudentId = new StudentId("A0000000Z"); // not matching any in list
        RepoCommand command = new RepoCommand(invalidStudentId, "user", "repo", null);

        assertThrows(CommandException.class, () -> command.execute(modelStub));
    }

    @Test
    public void execute_invalidUsernameOrRepo_setsNoRepository() throws Exception {
        Person person = new PersonBuilder().withStudentId("A3333333Z").build();
        ModelStubWithPersonList modelStub = new ModelStubWithPersonList(person);
        String studentId = person.getStudentId().value;

        RepoCommand command = new RepoCommand(new StudentId(studentId), "bad_user!", "repo", null);
        command.execute(modelStub);

        Person updated = modelStub.getLastEditedPerson();
        assertEquals(Repository.NO_REPOSITORY, updated.getRepository().toString());
    }

    @Test
    public void equals() {
        Index index = Index.fromOneBased(1);
        StudentId studentId = new StudentId("A1234567Z");
        String username = "ValidUser";
        String repositoryName = "valid-repo";
        Repository repo = new Repository("https://github.com/ValidUser/valid-repo");

        // same values -> returns true
        RepoCommand commandA = new RepoCommand(index, username, repositoryName, repo);
        RepoCommand commandACopy = new RepoCommand(index, username, repositoryName, repo);
        assertTrue(commandA.equals(commandACopy));

        // same object -> returns true
        assertTrue(commandA.equals(commandA));

        // null -> returns false
        assertFalse(commandA.equals(null));

        // different type -> returns false
        assertFalse(commandA.equals(new ClearCommand()));

        // different index -> returns false
        RepoCommand differentIndex = new RepoCommand(Index.fromOneBased(2), username, repositoryName, repo);
        assertFalse(commandA.equals(differentIndex));

        // different username -> returns false
        RepoCommand differentUsername = new RepoCommand(index, "OtherUser", repositoryName, repo);
        assertFalse(commandA.equals(differentUsername));

        // different repository name -> returns false
        RepoCommand differentRepoName = new RepoCommand(index, username, "other-repo", repo);
        assertFalse(commandA.equals(differentRepoName));

        // different constructor path: studentId instead of index -> returns false
        RepoCommand studentIdCommand = new RepoCommand(studentId, username, repositoryName, repo);
        assertFalse(commandA.equals(studentIdCommand));
    }


    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateSortedPersonList(Comparator<Person> comparator) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasTimedEvent(TimedEvent timedEvent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addTimedEvent(TimedEvent timedEvent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteTimedEvent(TimedEvent timedEvent) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<TimedEvent> getTimedEventList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<TimedEvent> getFilteredTimedEventList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredTimedEventList(Predicate<TimedEvent> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateSortedTimedEventList(Comparator<TimedEvent> comparator) {
            throw new AssertionError("This method should not be called.");
        }
    }

    private class ModelStubWithPersonList extends ModelStub {
        private final ObservableList<Person> internalList = FXCollections.observableArrayList();
        private Person lastSetTarget;
        private Person lastSetEdited;

        ModelStubWithPersonList(Person... persons) {
            internalList.addAll(Arrays.asList(persons));
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return internalList;
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            requireNonNull(target);
            requireNonNull(editedPerson);
            this.lastSetTarget = target;
            this.lastSetEdited = editedPerson;

            int index = internalList.indexOf(target);
            if (index == -1) {
                throw new AssertionError("Person not found in list");
            }
            internalList.set(index, editedPerson);
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            // no filtering logic for test
        }

        public Person getLastEditedPerson() {
            return lastSetEdited;
        }
    }
}
