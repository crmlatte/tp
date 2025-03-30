package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.exceptions.DuplicateTimedEventException;

/**
 * Assigns a timed event to one or more students identified by their index, student ID, or class number.
 */
public class AssignCommand extends Command {

    public static final String COMMAND_WORD = "assign";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Assigns a timed event to one or more students identified by by the index number "
            + "used in the displayed person list., student ID, or class number\n"
            + "Parameters: TIMED_EVENT_INDEX (must be a positive integer) "
            + "STUDENT_INDEX (must be a positive integer) or STUDENT_ID or CLASS_NUMBER\n"
            + "Example: " + COMMAND_WORD + " 1 2"
            + " or: " + COMMAND_WORD + " 1 A1234567B"
            + " or: " + COMMAND_WORD + " 1 T01";

    public static final String MESSAGE_ASSIGN_SUCCESS = "Assigned timed event to student: %1$s";
    public static final String MESSAGE_DUPLICATE_ASSIGNMENT = "This assignment is already assigned to the student";
    public static final String MESSAGE_NO_STUDENTS_IN_CLASS = "No students found in class: %1$s";

    private final Index timedEventIndex;
    private final Index studentIndex;
    private final StudentId studentId;
    private final ClassNumber classNumber;

    /**
     * Creates an AssignCommand using a timed event index and a student index.
     */
    public AssignCommand(Index timedEventIndex, Index studentIndex) {
        requireNonNull(timedEventIndex);
        requireNonNull(studentIndex);
        this.timedEventIndex = timedEventIndex;
        this.studentIndex = studentIndex;
        this.studentId = null;
        this.classNumber = null;
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
        this.classNumber = null;
    }

    /**
     * Creates an AssignCommand using a timed event index and a class number.
     */
    public AssignCommand(Index timedEventIndex, ClassNumber classNumber) {
        requireNonNull(timedEventIndex);
        requireNonNull(classNumber);
        this.timedEventIndex = timedEventIndex;
        this.studentIndex = null;
        this.studentId = null;
        this.classNumber = classNumber;
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

        StringBuilder resultMessage = new StringBuilder();

        if (studentIndex != null) {
            // Assign to single student by index
            if (studentIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            Person targetStudent = lastShownList.get(studentIndex.getZeroBased());
            try {
                targetStudent.addTimedEvent(targetEvent);
                model.setPerson(targetStudent, targetStudent);
                resultMessage.append(String.format(MESSAGE_ASSIGN_SUCCESS, Messages.format(targetStudent)));
            } catch (DuplicateTimedEventException e) {
                resultMessage.append(String.format(MESSAGE_DUPLICATE_ASSIGNMENT));
            }
        } else if (studentId != null) {
            // Assign to single student by ID
            Optional<Person> personOptional = lastShownList.stream()
                    .filter(person -> person.getStudentId().equals(studentId))
                    .findFirst();

            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + studentId);
            }
            Person targetStudent = personOptional.get();
            try {
                targetStudent.addTimedEvent(targetEvent);
                resultMessage.append(String.format(MESSAGE_ASSIGN_SUCCESS, Messages.format(targetStudent)));
                model.setPerson(targetStudent, targetStudent);
            } catch (DuplicateTimedEventException e) {
                resultMessage.append(String.format(MESSAGE_DUPLICATE_ASSIGNMENT));
            }
        } else if (classNumber != null) {
            // Assign to all students in class using streams
            List<Person> studentsInClass = lastShownList.stream()
                    .filter(person -> person.getClassNumber().equals(classNumber))
                    .toList();
            if (studentsInClass.isEmpty()) {
                throw new CommandException(String.format(MESSAGE_NO_STUDENTS_IN_CLASS, classNumber));
            }

            // Process each student and collect results
            String results = studentsInClass.stream()
                    .map(student -> {
                        try {
                            student.addTimedEvent(targetEvent);
                            model.setPerson(student, student);
                            return String.format(MESSAGE_ASSIGN_SUCCESS, Messages.format(student));
                        } catch (DuplicateTimedEventException e) {
                            return MESSAGE_DUPLICATE_ASSIGNMENT;
                        }
                    })
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");

            resultMessage.append(results);
        }

        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(resultMessage.toString());
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
        return Objects.equals(timedEventIndex, otherAssignCommand.timedEventIndex)
                && Objects.equals(studentIndex, otherAssignCommand.studentIndex)
                && Objects.equals(studentId, otherAssignCommand.studentId)
                && Objects.equals(classNumber, otherAssignCommand.classNumber);
    }
}
