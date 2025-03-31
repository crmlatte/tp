package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import tassist.address.logic.commands.ImportCommand;

/**
 * Parses input arguments and creates a new ImportCommand object
 */
public class ImportCommandParser implements Parser<ImportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ImportCommand
     * and returns an ImportCommand object for execution.
     */
    @Override
    public ImportCommand parse(String args) {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        Path filePath = ParserUtil.parseFilePath(trimmedArgs);

        return new ImportCommand(filePath);
    }
}
