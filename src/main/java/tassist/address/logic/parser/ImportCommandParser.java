package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;

import java.nio.file.Files;
import java.nio.file.Path;

import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.ImportCommand;
import tassist.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ImportCommand
     * and returns an ImportCommand object for execution.
     */
    @Override
    public ImportCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ImportCommand.MESSAGE_USAGE));
        }

        try {
            Path filePath = ParserUtil.parseFilePath(trimmedArgs);

            if (!Files.exists(filePath)) {
                throw new ParseException(MESSAGE_INVALID_FILE_PATH);
            }

            return new ImportCommand(filePath);
        } catch (IllegalValueException ive) {
            throw new ParseException(MESSAGE_INVALID_FILE_PATH);
        }
    }
}
