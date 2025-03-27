package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.logging.Logger;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.OpenCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new OpenCommand object
 */
public class OpenCommandParser implements Parser<OpenCommand> {
    private static final Logger logger = Logger.getLogger(OpenCommandParser.class.getName());

    /**
     * Parses the given {@code String} of arguments in the context of the OpenCommand
     * and returns an OpenCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public OpenCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        if (StudentId.isValidStudentId(trimmedArgs)) {
            logger.info("Parsing OpenCommand using student ID: " + trimmedArgs);
            return new OpenCommand(ParserUtil.parseStudentId(trimmedArgs));
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            logger.info("Parsing OpenCommand using index: " + trimmedArgs);
            return new OpenCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, OpenCommand.MESSAGE_USAGE), pe);
        }
    }
}
