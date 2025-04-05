package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;

import java.util.Objects;
import java.util.function.Predicate;

import tassist.address.commons.util.ToStringBuilder;
import tassist.address.model.Model;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;

/**
 * Finds and lists all persons in address book whose name,
 * studentId, or class number matches any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive), whose student ID matches exactly, "
            + "or whose class number matches exactly, and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]... or STUDENTID or CLASS\n"
            + "Example:\n"
            + COMMAND_WORD + " alice bob charlie\n"
            + COMMAND_WORD + " A1234567B" + " or " + COMMAND_WORD + " T01";

    private final NameContainsKeywordsPredicate namePredicate;
    private final Predicate<Person> studentIdPredicate;
    private final Predicate<Person> classNumberPredicate;

    /**
     * Constructs a FindCommand that filters by student ID.
     *
     * @param studentIdPredicate A predicate that returns true if the student ID matches the input exactly.
     */
    public FindCommand(Predicate<Person> studentIdPredicate) {
        this.studentIdPredicate = studentIdPredicate;
        this.namePredicate = null;
        this.classNumberPredicate = null;
    }

    /**
     * Constructs a FindCommand that filters by name keywords.
     *
     * @param namePredicate A predicate that returns true if any part of the person's name matches the given
     *                      keywords (case-insensitive).
     */
    public FindCommand(NameContainsKeywordsPredicate namePredicate) {
        this.namePredicate = namePredicate;
        this.studentIdPredicate = null;
        this.classNumberPredicate = null;
    }

    /**
     * Constructs a FindCommand that filters by class number.
     *
     * @param classNumberPredicate A predicate that returns true if the class number matches exactly.
     */
    public FindCommand(Predicate<Person> classNumberPredicate, boolean isClassNumber) {
        this.classNumberPredicate = classNumberPredicate;
        this.namePredicate = null;
        this.studentIdPredicate = null;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (studentIdPredicate != null) {
            model.updateFilteredPersonList(studentIdPredicate);
            return new CommandResult(
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
        } else if (namePredicate != null) {
            model.updateFilteredPersonList(namePredicate);
            return new CommandResult(
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
        } else if (classNumberPredicate != null) {
            model.updateFilteredPersonList(classNumberPredicate);
            return new CommandResult(
                    String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
        } else {
            //won't reach this line, throwing an assertion just in case
            throw new AssertionError("Either name keywords, student ID, or class number must be provided");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return Objects.equals(studentIdPredicate, otherFindCommand.studentIdPredicate)
                && Objects.equals(namePredicate, otherFindCommand.namePredicate)
                && Objects.equals(classNumberPredicate, otherFindCommand.classNumberPredicate);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (studentIdPredicate != null) {
            builder.add("studentIdPredicate", studentIdPredicate);
        } else if (namePredicate != null) {
            builder.add("namePredicate", namePredicate);
        } else if (classNumberPredicate != null) {
            builder.add("classNumberPredicate", classNumberPredicate);
        }
        return builder.toString();
    }
}
