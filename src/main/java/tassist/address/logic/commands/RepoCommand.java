package tassist.address.logic.commands;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;
import tassist.address.model.person.Repository;
import tassist.address.model.person.StudentId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

public class RepoCommand extends Command {

    public static final String COMMAND_WORD = "repo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the Repository of the person identified by the STUDENTID or INDEX."
            + "Existing github will be overwritten by the input.\n"
            + "Parameters: STUDENTID or INDEX, un/{username}, rn/{repository name}\n"
            + "Example:\n"
            + " 1." + COMMAND_WORD + " 2 un/Group-4 rn/WealthVault\n"
            + " 2." + COMMAND_WORD + " AxxxxxxxN un/Tutorial-G08 rn/BestApp";

    public static final String VALID_USERNAME_REGEX = "[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*";
    public static final String VALID_REPOSITORY_REGEX = "[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?";

    public static final String MESSAGE_ADD_REPOSITORY_SUCCESS = "Added Repository to Person: %1$s";
    public static final String MESSAGE_DELETE_GITHUB_SUCCESS = "Removed Repository from Person: %1$s";

    //allow multiple people to have the same repository (team repo)
    public static final String MESSAGE_INVALID_USERNAME = "Invalid Username!";
    public static final String MESSAGE_INVALID_REPOSITORY_NAME = "Invalid Repository Name!";
    public static final String MESSAGE_MISSING_USERNAME = "Please enter Username after un/";
    public static final String MESSAGE_MISSING_REPOSITORY_NAME = "Please enter Repository Name after rn/";

    public final String username;
    public final String repositoryName;

    public final Index index;

    public final StudentId studentId;

    /**
     * @param studentId of the person in the list to edit the repository of
     * @param username used to update username
     * @param repositoryName used to update repositoryName
     */
    public RepoCommand(StudentId studentId, String username, String repositoryName) {
        requireNonNull(studentId);
        requireNonNull(username);
        requireNonNull(repositoryName);
        this.studentId = studentId;
        this.username = username;
        this.repositoryName = repositoryName;
        this.index = null;
    }

    /**
     * @param index of the person in the list to edit the repository of
     * @param username used to update username
     * @param repositoryName used to update repositoryName
     */
    public RepoCommand( Index index, String username, String repositoryName) {
        requireNonNull(index);
        requireNonNull(username);
        requireNonNull(repositoryName);
        this.studentId = null;
        this.username = username;
        this.repositoryName = repositoryName;
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToEdit = null;

        if (index != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToEdit = lastShownList.get(index.getZeroBased());
        }
        if (studentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream().filter(
                    person -> person.getStudentId().equals(studentId)).findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + studentId);
            }
            personToEdit = personOptional.get();
        }

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getClassNumber(),
                personToEdit.getStudentId(),
                personToEdit.getGithub(),
                personToEdit.getProjectTeam(),
                createRepo(username, repositoryName),
                personToEdit.getTags(),
                personToEdit.getProgress());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(MESSAGE_ADD_REPOSITORY_SUCCESS);
    }

    public static Repository createRepo(String username, String repositoryName) {
        String un;
        String rn;

        try {
            if (username != null && username.matches(VALID_USERNAME_REGEX)) {
                un = username;
            } else {
                throw new IllegalArgumentException(MESSAGE_INVALID_USERNAME);
            }

            if (repositoryName != null && repositoryName.matches(VALID_REPOSITORY_REGEX)) {
                rn = repositoryName;
            } else {
                throw new IllegalArgumentException(MESSAGE_INVALID_REPOSITORY_NAME);
            }

            String fullRepository = "https://github.com/" + un + "/" + rn;
            return new Repository(fullRepository);

        } catch (IllegalArgumentException e) {
            return new Repository(Repository.NO_REPOSITORY);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RepoCommand)) {
            return false;
        }

        RepoCommand e = (RepoCommand) other;
        return Objects.equals(studentId, e.studentId)
                && Objects.equals(index, e.index)
                && Objects.equals(username, e.username)
                && Objects.equals(repositoryName, e.repositoryName);
    }
}
