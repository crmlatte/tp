package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.ExportCommand.MESSAGE_EXPORT_FAILURE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.ExportCommand;
import tassist.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ExportCommand object
 */
public class ExportCommandParser implements Parser<ExportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ExportCommand
     * and returns an ExportCommand object for execution.
     */
    @Override
    public ExportCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ExportCommand.MESSAGE_USAGE));
        }

        try {
            Path filePath = ParserUtil.parseFilePath(trimmedArgs);

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            return new ExportCommand(filePath);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_FILE_PATH,
                    ExportCommand.MESSAGE_USAGE));
        } catch (IOException e) {
            throw new ParseException(String.format(MESSAGE_EXPORT_FAILURE,
                    ExportCommand.MESSAGE_USAGE));
        }
    }
}
