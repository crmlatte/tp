package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.ProgressCommand.MESSAGE_USAGE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.ProgressCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new ProgressCommand object
 */
public class ProgressCommandParser implements Parser<ProgressCommand> {

    private static final Logger logger = Logger.getLogger(ClassCommandParser.class.getName());

    /**
     * Parses the given {@code String} of arguments in the context of the ProgressCommand
     * and returns a ProgressCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ProgressCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PROGRESS);
        String preamble = argMultimap.getPreamble().trim();

        if (preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PROGRESS);

        String progressString = argMultimap.getValue(PREFIX_PROGRESS)
                .filter(value -> !value.isEmpty()).orElse(null);

        if (progressString == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        Progress progress;
        try {
            progress = new Progress(progressString);
        } catch (IllegalArgumentException e) {
            throw new ParseException(Progress.MESSAGE_CONSTRAINTS, e);
        }

        if (preamble.matches(VALIDATION_REGEX)) {
            try {
                logger.info("Parsing ClassCommand using student ID: " + preamble);
                StudentId studentId = ParserUtil.parseStudentId(preamble);
                return new ProgressCommand(studentId, progress);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), ive);
            }
        }

        Index index;
        try {
            logger.info("Parsing ClassCommand using index: " + preamble);
            index = ParserUtil.parseIndex(preamble);
            return new ProgressCommand(index, progress);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ProgressCommand.MESSAGE_USAGE), ive);
        }
    }
}
