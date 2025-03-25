package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.parser.CliSyntax.PREFIX_DATE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.timedevents.Assignment;
import tassist.address.model.timedevents.UniqueTimedEventList;

/**
 * Adds an assignment to the address book.
 */
public class AssignmentCommand extends Command {

    public static final String COMMAND_WORD = "assignment";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an assignment to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_DATE + "DATE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "CS2103T Project "
            + PREFIX_DATE + "30-01-2025";

    public static final String MESSAGE_SUCCESS = "New assignment added: %1$s";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment already exists in the address book";
    public static final String MESSAGE_INVALID_DATE_FORMAT = "Invalid date format. Please use DD-MM-YY or DD-MM-YYYY";

    private final Assignment toAdd;

    /**
     * Creates an AssignmentCommand to add the specified {@code Assignment}
     */
    public AssignmentCommand(Assignment assignment) {
        requireNonNull(assignment);
        toAdd = assignment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasTimedEvent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_ASSIGNMENT);
        }

        model.addTimedEvent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.toString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignmentCommand)) {
            return false;
        }

        AssignmentCommand otherAssignmentCommand = (AssignmentCommand) other;
        return toAdd.equals(otherAssignmentCommand.toAdd);
    }
} 