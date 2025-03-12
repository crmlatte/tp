package tassist.address.logic.commands;

import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;

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

    public static final String MESSAGE_NOT_IMPLEMENTED_YET =
            "Github command not implemented yet";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED_YET);
    }

}