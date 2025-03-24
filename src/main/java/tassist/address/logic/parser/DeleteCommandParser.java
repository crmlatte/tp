package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.DeleteCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argumentMultimap = ArgumentTokenizer.tokenize(args);
        String trimmedArgs = argumentMultimap.getPreamble().trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteCommand.MESSAGE_USAGE));
        }
        if (trimmedArgs.matches(VALIDATION_REGEX)) {
            try {
                StudentId studentId = ParserUtil.parseStudentId(trimmedArgs);
                return new DeleteCommand(studentId);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteCommand.MESSAGE_USAGE), ive);
            }
        }

        try {
            Index index = ParserUtil.parseIndex(trimmedArgs);
            return new DeleteCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteCommand.MESSAGE_USAGE), ive);
        }
    }
}
