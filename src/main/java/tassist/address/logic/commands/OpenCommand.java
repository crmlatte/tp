package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_PERSON_NOT_FOUND;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.util.ToStringBuilder;
import tassist.address.logic.Messages;
import tassist.address.logic.browser.BrowserService;
import tassist.address.logic.browser.DesktopBrowserService;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;

/**
 * Opens the GitHub link of a person identified using its displayed index from the address book.
 */
public class OpenCommand extends Command {

    public static final String COMMAND_WORD = "open";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens the GitHub link of the person identified by student ID or "
            + "the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) or STUDENTID\n"
            + "Example: " + COMMAND_WORD + " 1" + " or " + COMMAND_WORD + " A1234567B";

    public static final String MESSAGE_OPEN_GITHUB_SUCCESS = "Opening GitHub link for: %1$s";
    public static final String MESSAGE_OPEN_GITHUB_FAILURE = "Failed to open GitHub link for: %1$s";

    private static final Logger logger = Logger.getLogger(OpenCommand.class.getName());

    private final Index targetIndex;
    private final StudentId targetStudentId;
    private final BrowserService browserService;

    /**
     * Constructs an OpenCommand with a target index.
     *
     * @param targetIndex The index of the student whose GitHub link will be opened.
     */
    public OpenCommand(Index targetIndex) {
        this(targetIndex, new DesktopBrowserService());
    }

    /**
     * Constructs an OpenCommand with a student ID.
     *
     * @param studentId The student ID of the student whose GitHub link will be opened.
     */
    public OpenCommand(StudentId studentId) {
        this(studentId, new DesktopBrowserService());
    }

    /**
     * Constructs an OpenCommand with a target index and a specified browser service.
     *
     * @param targetIndex    The index of the student whose GitHub link will be opened.
     * @param browserService The browser service to handle opening URLs.
     */
    public OpenCommand(Index targetIndex, BrowserService browserService) {
        this.targetIndex = targetIndex;
        this.browserService = browserService;
        this.targetStudentId = null;
    }

    /**
     * Constructs an OpenCommand with a student ID and a specified browser service.
     *
     * @param studentId The student ID of the student whose GitHub link will be opened.
     * @param browserService The browser service to handle opening URLs.
     */
    public OpenCommand(StudentId studentId, BrowserService browserService) {
        this.targetStudentId = studentId;
        this.browserService = browserService;
        this.targetIndex = null;
    }

    /**
     * Gets the target index.
     *
     * @return The target index.
     */
    public Index getTargetIndex() {
        return targetIndex;
    }

    /**
     * Gets the target student ID.
     *
     * @return The target student ID.
     */
    public StudentId getTargetStudentId() {
        return targetStudentId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        logger.info("Executing Open Command with " + (targetIndex != null ? "Index: " + targetIndex : "Student ID: "
                + targetStudentId));
        Person personToOpen;

        if (targetIndex != null) {
            List<Person> lastShownList = model.getFilteredPersonList();
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToOpen = lastShownList.get(targetIndex.getZeroBased());
        } else if (targetStudentId != null) {
            Optional<Person> personOptional = model.getFilteredPersonList().stream()
                    .filter(person -> person.getStudentId().equals(targetStudentId)).findFirst();
            if (personOptional.isEmpty()) {
                throw new CommandException(MESSAGE_PERSON_NOT_FOUND + targetStudentId);
            }
            personToOpen = personOptional.get();
        } else {
            //won't reach this line, throwing an assertion just in case
            throw new AssertionError("Either index or student ID must be provided");
        }

        try {
            browserService.openUrl(personToOpen.getGithub().value);
            return new CommandResult(
                    String.format(MESSAGE_OPEN_GITHUB_SUCCESS, Messages.format(personToOpen))
            );
        } catch (IOException | URISyntaxException e) {
            return new CommandResult(
                    String.format(MESSAGE_OPEN_GITHUB_FAILURE, Messages.format(personToOpen))
            );
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof OpenCommand)) {
            return false;
        }

        OpenCommand otherOpenCommand = (OpenCommand) other;
        return Objects.equals(targetIndex, otherOpenCommand.targetIndex)
                && Objects.equals(targetStudentId, otherOpenCommand.targetStudentId);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (targetIndex != null) {
            builder.add("targetIndex", targetIndex);
        } else if (targetStudentId != null) {
            builder.add("targetStudentId", targetStudentId);
        }
        return builder.toString();
    }
}
