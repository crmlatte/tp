package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;

/**
 * Edits the progress value of an existing student.
 */
public class ProgressCommand extends Command {
    public static final String COMMAND_WORD = "progress";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Updates a student's progress "
            + "by the index in the displayed list or student ID.\n"
            + "Parameters: INDEX (must be a positive integer) or STUDENTID"
            + " + " + PREFIX_PROGRESS + "PROGRESS\n"
            + "Examples:\n"
            + "1. progress 1 pr/80\n"
            + "2. progress A1234567C pr/30\n";

    public static final String MESSAGE_SET_PROGRESS_SUCCESS = "Set progress for student: %1$s";

    private static final Logger logger = Logger.getLogger(ProgressCommand.class.getName());

    private final Index index;
    private final StudentId studentId;
    private final Progress progress;

    /**
     * Constructs an ProgressCommand with a student ID.
     *
     * @param index The index of the student in the currently displayed student list.
     * @param setProgress The progress value to be set for the student.
     */
    public ProgressCommand(Index index, Progress setProgress) {
        this.index = index;
        this.studentId = null;
        this.progress = setProgress;
    }

    /**
     * Constructs an ProgressCommand with a student ID.
     *
     * @param studentId The student ID of the student whose progress value will be updated.
     * @param setProgress The progress value to be set for the student.
     */
    public ProgressCommand(StudentId studentId, Progress setProgress) {
        this.index = null;
        this.studentId = studentId;
        this.progress = setProgress;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing ProgressCommand with " + (index != null ? "Index: " + index : "Student ID: "
                + studentId));
        Person personToEdit;

        if (index != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToEdit = lastShownList.get(index.getZeroBased());
        } else if (studentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream()
                    .filter(person -> person.getStudentId().equals(studentId)).findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(Messages.MESSAGE_PERSON_NOT_FOUND + studentId);
            }
            personToEdit = personOptional.get();
        } else {
            // won't reach this line, throwing an assertion just in case
            throw new AssertionError("Either index or student ID must be provided");
        }

        Person editedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getClassNumber(),
                personToEdit.getStudentId(),
                personToEdit.getGithub(),
                personToEdit.getProjectTeam(),
                personToEdit.getTags(),
                progress,
                personToEdit.getTimedEventsList()
        );

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(editedPerson));
    }

    /**
     * Generates a command execution success message based on whether
     * the progress is set
     */
    private String generateSuccessMessage(Person personToEdit) {
        return String.format(MESSAGE_SET_PROGRESS_SUCCESS, Messages.format(personToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ProgressCommand)) {
            return false;
        }

        ProgressCommand o = (ProgressCommand) other;
        return Objects.equals(index, o.index)
                && Objects.equals(studentId, o.studentId)
                && Objects.equals(progress, o.progress);
    }

}

