package tassist.address.logic.commands;

import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Github;
import tassist.address.model.person.StudentId;

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

    public static final String MESSAGE_ARGUMENTS = "Student id: %1$d, Github: %2$s";
    private final StudentId studentId;
    private final Github github;

    public GithubCommand(StudentId studentId, Github github) {
        this.studentId = studentId;
        this.github = github;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(
                String.format(MESSAGE_ARGUMENTS, studentId, github));
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
        return studentId.equals(e.studentId)
                && github.equals(e.github);
    }

}