package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.util.ToStringBuilder;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;

/**
 * Deletes a person identified using it's displayed index from the address book.
 */
public class DeleteCommand extends Command implements ConfirmableCommand {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number "
            + "used in the displayed person list, or studentId.\n"
            + "Parameters: INDEX (must be a positive integer) or STUDENTID\n"
            + "Example: " + COMMAND_WORD + " 1"
            + " or: " + COMMAND_WORD + " A1234567B";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_CONFIRM_DELETE = "Are you sure you want to delete: %s? (Y/N)";
    public static final String MESSAGE_DELETE_CANCELLED = "Deletion cancelled.";

    private final Index targetIndex;
    private final StudentId targetStudentId;

    /**
     * Constructs a {@code DeleteCommand} using a specified
     * index to identify the target Student
     * @param targetIndex of person to be deleted
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
        this.targetStudentId = null;
    }

    /**
     * Constructs a {@code DeleteCommand} using a specified
     * studentId to identify the target Student
     * @param targetstudentId of person to be deleted
     */
    public DeleteCommand(StudentId targetstudentId) {
        this.targetStudentId = targetstudentId;
        this.targetIndex = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToDelete = null;

        if (targetIndex != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToDelete = lastShownList.get(targetIndex.getZeroBased());
        }
        if (targetStudentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream().filter(
                    person -> person.getStudentId().equals(targetStudentId)).findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + targetStudentId);
            }
            personToDelete = personOptional.get();
        }
        return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)), this);
    }

    @Override
    public CommandResult executeConfirmed(Model model) {
        requireNonNull(model);
        Person personToDelete = null;

        if (targetIndex != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToDelete = lastShownList.get(targetIndex.getZeroBased());
        }
        if (targetStudentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream().filter(
                    person -> person.getStudentId().equals(targetStudentId)).findFirst();
            if (personOptional.isEmpty()) {
                return new CommandResult(Messages.MESSAGE_PERSON_NOT_FOUND + targetStudentId);
            }
            personToDelete = personOptional.get();
        }
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public String getConfirmationMessage() {
        return MESSAGE_CONFIRM_DELETE;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return Objects.equals(targetIndex, otherDeleteCommand.targetIndex)
                && Objects.equals(targetStudentId, otherDeleteCommand.targetStudentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
