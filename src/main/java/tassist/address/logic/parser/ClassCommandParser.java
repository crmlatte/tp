package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.ClassCommand.MESSAGE_USAGE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.model.person.ClassNumber.DEFAULT_CLASS;
import static tassist.address.model.person.ClassNumber.MESSAGE_CONSTRAINTS;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.ClassCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new ClassCommand object
 */
public class ClassCommandParser implements Parser<ClassCommand> {
    public static final String MESSAGE_REMOVE_CLASS = "Please use `c/` to unassign a class. e.g., class 1 c/";
    private static final Logger logger = Logger.getLogger(ClassCommandParser.class.getName());
    /**
     * Parses the given {@code String} of arguments in the context of the ClassCommand
     * and returns a ClassCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ClassCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_CLASS);
        String preamble = argMultimap.getPreamble().trim();

        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClassCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_CLASS);

        String classNumberString = argMultimap.getValue(PREFIX_CLASS).orElse("");

        if ("No tutorial assigned".equalsIgnoreCase(classNumberString)) {
            throw new ParseException(MESSAGE_REMOVE_CLASS);
        }

        if (classNumberString.isEmpty()) {
            classNumberString = DEFAULT_CLASS;
        }

        ClassNumber classNumber;
        try {
            classNumber = new ClassNumber(classNumberString);
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_CONSTRAINTS, e);
        }

        if (preamble.matches(VALIDATION_REGEX)) {
            try {
                logger.info("Parsing ClassCommand using student ID: " + preamble);
                StudentId studentId = ParserUtil.parseStudentId(preamble);
                return new ClassCommand(studentId, classNumber);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), ive);
            }
        }

        Index index;
        try {
            logger.info("Parsing ClassCommand using index: " + preamble);
            index = ParserUtil.parseIndex(preamble);
            return new ClassCommand(index, classNumber);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ClassCommand.MESSAGE_USAGE), ive);
        }
    }
}
