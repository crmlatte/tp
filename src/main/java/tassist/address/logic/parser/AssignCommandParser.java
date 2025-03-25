package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.AssignCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new AssignCommand object
 */
public class AssignCommandParser implements Parser<AssignCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AssignCommand
     * and returns an AssignCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AssignCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);
        String trimmedArgs = argMultimap.getPreamble().trim();
        String[] argArray = trimmedArgs.split("\\s+");

        if (argArray.length != 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignCommand.MESSAGE_USAGE));
        }

        // Parse timed event index
        Index timedEventIndex;
        try {
            timedEventIndex = ParserUtil.parseIndex(argArray[0]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignCommand.MESSAGE_USAGE), ive);
        }

        // Try to parse as student ID first
        if (argArray[1].matches(VALIDATION_REGEX)) {
            try {
                StudentId studentId = ParserUtil.parseStudentId(argArray[1]);
                return new AssignCommand(timedEventIndex, studentId);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        AssignCommand.MESSAGE_USAGE), ive);
            }
        }

        // If not a valid student ID, try to parse as index
        try {
            Index studentIndex = ParserUtil.parseIndex(argArray[1]);
            return new AssignCommand(timedEventIndex, studentIndex);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignCommand.MESSAGE_USAGE), ive);
        }
    }
} 