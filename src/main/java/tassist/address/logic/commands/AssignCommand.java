package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.exceptions.DuplicateTimedEventException;

/**
 * Assigns a timed event to one or more students identified by their index or student ID.
 */
public class AssignCommand extends Command {

    public static final String COMMAND_WORD = "assign";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns a timed event to one or more students identified by their index or student ID.\n"
            + "Parameters: TIMED_EVENT_INDEX (must be a positive integer) "
            + "STUDENT_INDEX (must be a positive integer) or STUDENT_ID\n"
            + "Example: " + COMMAND_WORD + " 1 2"
            + " or: " + COMMAND_WORD + " 1 A1234567B";

    public static final String MESSAGE_ASSIGN_SUCCESS = "Assigned timed event to student: %1$s";
    public static final String MESSAGE_ASSIGN_FAILURE = "Failed to assign timed event to student: %1$s";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment is already assigned to the student";

    private final Index timedEventIndex;
    private final Index studentIndex;
    private final StudentId studentId;

    /**
     * Creates an AssignCommand using a timed event index and a student index.
     */
    public AssignCommand(Index timedEventIndex, Index studentIndex) {
        requireNonNull(timedEventIndex);
        requireNonNull(studentIndex);
        this.timedEventIndex = timedEventIndex;
        this.studentIndex = studentIndex;
        this.studentId = null;
    }

    /**
     * Creates an AssignCommand using a timed event index and a student ID.
     */
    public AssignCommand(Index timedEventIndex, StudentId studentId) {
        requireNonNull(timedEventIndex);
        requireNonNull(studentId);
        this.timedEventIndex = timedEventIndex;
        this.studentIndex = null;
        this.studentId = studentId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<TimedEvent> timedEvents = model.getTimedEventList();
        List<Person> lastShownList = model.getFilteredPersonList();

        // Check if timed event index is valid
        if (timedEventIndex.getZeroBased() >= timedEvents.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TIMED_EVENT_DISPLAYED_INDEX);
        }
        TimedEvent targetEvent = timedEvents.get(timedEventIndex.getZeroBased());

        // Find target student(s)
        Person targetStudent = null;
        if (studentIndex != null) {
            if (studentIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            targetStudent = lastShownList.get(studentIndex.getZeroBased());
        } else if (studentId != null) {
            Optional<Person> personOptional = lastShownList.stream()
                    .filter(person -> person.getStudentId().equals(studentId))
                    .findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + studentId);
            }
            targetStudent = personOptional.get();
        }

        try {
            // Add timed event to student's list
            targetStudent.addTimedEvent(targetEvent);
            
            // Update the model to reflect changes
            model.setPerson(targetStudent, targetStudent);
            model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
            
            return new CommandResult(String.format(MESSAGE_ASSIGN_SUCCESS, Messages.format(targetStudent)));
        } catch (DuplicateTimedEventException e) {
            throw new CommandException(MESSAGE_DUPLICATE_ASSIGNMENT);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AssignCommand)) {
            return false;
        }

        AssignCommand otherAssignCommand = (AssignCommand) other;
        return timedEventIndex.equals(otherAssignCommand.timedEventIndex)
                && ((studentIndex != null && studentIndex.equals(otherAssignCommand.studentIndex))
                || (studentId != null && studentId.equals(otherAssignCommand.studentId)));
    }
} 