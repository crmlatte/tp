package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.timedevents.TimedEvent;

/**
 * Unassigns a timed event from all students and deletes it from the event list.
 */
public class UnassignCommand extends Command implements ConfirmableCommand {
    public static final String COMMAND_WORD = "unassign";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unassigns a timed event from all students and deletes it from the event list.\n"
            + "Parameters: INDEX\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNASSIGN_EVENT_SUCCESS = "Unassigned timed event: %1$s";
    public static final String MESSAGE_CONFIRM = "Are you sure you want to "
            + "unassign the timed event '%1$s' from all students? (Y/N)";
    public static final String MESSAGE_UNASSIGN_CANCELLED = "Action cancelled.";

    private final Index targetIndex;
    private Model model;

    public UnassignCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        this.model = model;
        List<TimedEvent> lastShownList = model.getTimedEventList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX);
        }
        TimedEvent eventToUnassign = lastShownList.get(targetIndex.getZeroBased());
        return new CommandResult(String.format(MESSAGE_CONFIRM, eventToUnassign.getName()), this);
    }

    @Override
    public CommandResult executeConfirmed(Model model) {
        requireNonNull(model);
        List<TimedEvent> lastShownList = model.getTimedEventList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            return new CommandResult(Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX);
        }
        TimedEvent eventToUnassign = lastShownList.get(targetIndex.getZeroBased());

        // Remove the event from all students who have it
        model.getFilteredPersonList().stream()
                .filter(person -> person.hasTimedEvent(eventToUnassign))
                .forEach(person -> {
                    person.removeTimedEvent(eventToUnassign);
                    model.setPerson(person, person); // Trigger UI update
                });

        // Remove the event from the event list
        model.deleteTimedEvent(eventToUnassign);
        return new CommandResult(String.format(MESSAGE_UNASSIGN_EVENT_SUCCESS, eventToUnassign.getName()));
    }

    @Override
    public String getConfirmationMessage() {
        List<TimedEvent> lastShownList = model.getTimedEventList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            return Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX;
        }
        TimedEvent eventToUnassign = lastShownList.get(targetIndex.getZeroBased());
        return String.format(MESSAGE_CONFIRM, eventToUnassign.getName());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UnassignCommand // instanceof handles nulls
                && targetIndex.equals(((UnassignCommand) other).targetIndex)); // state check
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("UnassignCommand{")
                .append("targetIndex=").append(targetIndex)
                .append("}")
                .toString();
    }
}
