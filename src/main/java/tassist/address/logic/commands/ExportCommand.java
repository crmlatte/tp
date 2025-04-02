package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.storage.CsvJsonConverter;

/**
 * Exports the current address book data to a CSV file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Exports the current address book data to a CSV file.\n"
            + "Parameters: FILE_PATH (must be a valid absolute path to the CSV file)\n"
            + "Example (absolute path on Unix/mac): " + COMMAND_WORD + " /Users/Name/Downloads/file.csv "
            + "or (Windows): " + COMMAND_WORD + " C:\\Users\\Name\\Downloads\\file.csv";

    public static final String MESSAGE_EXPORT_SUCCESS = "Successfully exported address book to: %1$s";
    public static final String MESSAGE_EXPORT_FAILURE = "Failed to export address book to: %1$s";

    private final Path filePath;

    /**
     * Constructs an ExportCommand with a specified file path.
     *
     * @param filePath The destination path of the CSV file to be created.
     */
    public ExportCommand(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (filePath == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_FILE_PATH);
        }

        if (!isCsvFile(filePath.toString())) {
            throw new CommandException(Messages.MESSAGE_INVALID_FILE_FORMAT);
        }

        try {
            CsvJsonConverter converter = new CsvJsonConverter();
            converter.convertJsonToCsv(filePath, model.getAddressBook());

            return new CommandResult(generateSuccessMessage());
        } catch (IOException e) {
            // should not reach here, throwing an exception just in case
            throw new CommandException("Invalid File Path", e);
        }
    }

    private boolean isCsvFile(String filePath) {
        return filePath.endsWith(".csv");
    }

    /**
     * Generates a command execution success message based on whether
     * the address book is exported
     */
    private String generateSuccessMessage() {
        String message = filePath != null ? MESSAGE_EXPORT_SUCCESS : MESSAGE_EXPORT_FAILURE;
        return String.format(message, filePath);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ExportCommand)) {
            return false;
        }

        ExportCommand otherExportCommand = (ExportCommand) other;
        return filePath.equals(otherExportCommand.filePath);
    }
}
