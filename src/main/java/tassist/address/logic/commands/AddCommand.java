package tassist.address.logic.commands;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tassist.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROJECT_TEAM;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY;
import static tassist.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static tassist.address.logic.parser.CliSyntax.PREFIX_TAG;

import tassist.address.commons.util.ToStringBuilder;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Github;
import tassist.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to the student list.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_STUDENT_ID + "STUDENTID "
            + "[" + PREFIX_GITHUB + "GITHUB] "
            + "[" + PREFIX_PROJECT_TEAM + "TEAM] "
            + "[" + PREFIX_REPOSITORY + "REPOSITORY] "
            + "[" + PREFIX_CLASS + "CLASS_NUMBER] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_PROGRESS + "PROGRESS]\n"
            + "Example:\n"
            + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@u.nus.edu "
            + PREFIX_STUDENT_ID + "A0000000B "
            + PREFIX_PROJECT_TEAM + "TAssist "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney "
            + PREFIX_PROGRESS + "50\n\n"
            + "To add a student, minimally NAME, EMAIL, PHONE, STUDENTID must be present.\n"
            + "GITHUB, TEAM, REPOSITORY, CLASS_NUMBER, TAG and PROGRESS are optional fields and can be omitted out";

    public static final String MESSAGE_SUCCESS = "New student added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.\n"
            + "Error : Duplicate StudentId.\n"
            + "Please check if you have typed the StudentId correctly.";
    public static final String MESSAGE_EXISTING_PHONE = "Error! This phone number belongs to another student.";
    public static final String MESSAGE_EXISTING_EMAIL = "Error! This email belongs to another student.";
    public static final String MESSAGE_EXISTING_GITHUB = "Error! This GitHub belongs to another student.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
        assert (!isNull(person));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        boolean emailExists = model.getFilteredPersonList().stream()
                .anyMatch(person -> person.getEmail().equals(toAdd.getEmail()));
        boolean phoneExists = model.getFilteredPersonList().stream()
                .anyMatch(person -> person.getPhone().equals(toAdd.getPhone()));
        boolean githubExists = !toAdd.getGithub().value.equals(Github.NO_GITHUB)
                && model.getFilteredPersonList().stream()
                .anyMatch(person -> person.getGithub().equals(toAdd.getGithub()));
        if (emailExists) {
            throw new CommandException(MESSAGE_EXISTING_EMAIL);
        }
        if (phoneExists) {
            throw new CommandException(MESSAGE_EXISTING_PHONE);
        }
        if (githubExists) {
            throw new CommandException(MESSAGE_EXISTING_GITHUB);
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
