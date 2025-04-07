package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.function.Predicate;

import tassist.address.logic.commands.FindCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Name;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (trimmedArgs.matches(StudentId.VALIDATION_REGEX)) {
            Predicate<Person> studentIdPredicate = person -> person.getStudentId().value.equals(trimmedArgs);
            return new FindCommand(studentIdPredicate);
        }

        // Check if the input matches a class number format
        if (trimmedArgs.matches(ClassNumber.VALIDATION_REGEX) || trimmedArgs.equals(ClassNumber.DEFAULT_CLASS)) {
            Predicate<Person> classNumberPredicate = person -> person.getClassNumber().value.equals(trimmedArgs);
            return new FindCommand(classNumberPredicate, true);
        }

        String normalizedArgs = args.trim().replaceAll("\\s+", " ");

        if (!normalizedArgs.matches(Name.VALIDATION_REGEX)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        return new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

}
