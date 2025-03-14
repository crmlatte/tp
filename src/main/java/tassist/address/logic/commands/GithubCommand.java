package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Github;
import tassist.address.model.person.Person;

/**
 * Changes the github of an existing person in the address book.
 */
public class GithubCommand extends Command {

    public static final String COMMAND_WORD = "github";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the github of the person identified "
            + "by the {Student id number}. "
            + "Existing github will be overwritten by the input.\n"
            + "Parameters: STUDENT_ID (must be at least 2 characters long, alphanumeric, no spaces) "
            + "g/[GITHUB_URL]\n"
            + "Example: " + COMMAND_WORD + " A1234567X "
            + "g/https://github.com/tammzz";

    public static final String MESSAGE_ADD_GITHUB_SUCCESS = "Added github to Person: %1$s";
    public static final String MESSAGE_DELETE_GITHUB_SUCCESS = "Removed github from Person: %1$s";
    private final Index index;
    private final Github github;

    /**
     * @param index of person
     * @param github of the person to be updated to
     */
    public GithubCommand(Index index, Github github) {
        requireNonNull(index);
        requireNonNull(github);

        this.index = index;
        this.github = github;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = new Person(
                personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), github, personToEdit.getTags());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the github is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Person personToEdit) {
        String message = !github.value.isEmpty() ? MESSAGE_ADD_GITHUB_SUCCESS : MESSAGE_DELETE_GITHUB_SUCCESS;
        return String.format(message, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GithubCommand)) {
            return false;
        }

        GithubCommand e = (GithubCommand) other;
        return index.equals(e.index)
                && github.equals(e.github);
    }

}
