package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.DeleteCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.StudentId;

import java.util.Optional;

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

        Optional<Index> index = tryParseIndex(args);
        if (index.isPresent()) {
            return new DeleteCommand(index.get());
        }
        Optional<StudentId> studentId = tryParseStudentId(args);
        if (studentId.isPresent()) {
            return new DeleteCommand(studentId.get());
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteCommand.MESSAGE_USAGE));
    }

    private Optional<Index> tryParseIndex(String args) {
        try {
            return Optional.of(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            return Optional.empty();
        }
    }

    private Optional<StudentId> tryParseStudentId(String args) {
        try {
            return Optional.of(ParserUtil.parseStudentId(args));
        } catch (ParseException pe) {
            return Optional.empty();
        }
    }

}
