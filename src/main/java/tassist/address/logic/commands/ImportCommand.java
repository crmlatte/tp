package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import tassist.address.commons.util.ToStringBuilder;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;

/**
 * Imports a CSV file containing a list of students into the address book.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Imports a CSV file containing a list of students into the address book.\n"
            + "Parameters: FILE_PATH (must be a valid absolute path to the CSV file)\n"
            + "Example (absolute path): " + COMMAND_WORD + " /path/to/file.csv";

    public static final String MESSAGE_IMPORT_SUCCESS = "Successfully imported CSV file: %1$s";
    public static final String MESSAGE_IMPORT_FAILURE = "Failed to import CSV file: %1$s";

    private final Path filePath;

    public ImportCommand(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (filePath != null) {
            Path resolvedPath = resolveFilePath(filePath);
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ImportCommand)) {
            return false;
        }

        ImportCommand otherImportCommand = (ImportCommand) other;
        return filePath.equals(otherImportCommand.filePath);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("filePath", filePath)
                .toString();
    }
}
