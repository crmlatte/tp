package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.UnassignCommand.MESSAGE_USAGE;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.UnassignCommand;
import tassist.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new UnassignCommand object
 */
public class UnassignCommandParser implements Parser<UnassignCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UnassignCommand
     * and returns a UnassignCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UnassignCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new UnassignCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE), pe);
        }
    }
} 