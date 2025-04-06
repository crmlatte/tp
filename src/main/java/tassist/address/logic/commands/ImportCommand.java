package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.io.IOException;
import java.nio.file.Path;

import com.opencsv.exceptions.CsvException;

import tassist.address.commons.exceptions.DataLoadingException;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.storage.CsvJsonConverter;
import tassist.address.storage.Storage;

/**
 * Imports a CSV file containing a list of students into the address book.
 */
public class ImportCommand extends Command {

    public static final String COMMAND_WORD = "import";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Imports a CSV file containing a list of students into the address book.\n"
            + "Parameters: FILE_PATH (must be a valid absolute path to the CSV file)\n"
            + "Example:\n"
            + "(Unix/mac): " + COMMAND_WORD + " /Users/Name/Downloads/file.csv\n"
            + "(Windows): " + COMMAND_WORD + " C:\\Users\\Name\\Downloads\\file.csv";

    public static final String MESSAGE_IMPORT_SUCCESS = "Successfully imported CSV file: %1$s";
    public static final String MESSAGE_INVALID_VALUE_IN_FILE = "Error during file conversion: invalid value";
    public static final String MESSAGE_UNABLE_TO_ACCESS_FILE = "Unable to access file";
    public static final String MESSAGE_CORRUPTED_FILE = "File is corrupted";
    private static Storage storage = null;

    private final Path filePath;

    /**
     * Constructs an ImportCommand with a specified file path.
     *
     * @param filePath The file path of the CSV file to be imported.
     */
    public ImportCommand(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (filePath == null) {
            throw new CommandException(MESSAGE_INVALID_FILE_PATH);
        }

        ReadOnlyAddressBook newData;

        try {
            CsvJsonConverter converter = new CsvJsonConverter();

            if (isCsvFile(filePath.toString())) {
                Path jsonFilePath = model.getAddressBookFilePath();
                converter.convertCsvToJson(filePath, jsonFilePath);
            } else {
                throw new CommandException(MESSAGE_INVALID_FILE_PATH); // Not CSV file type
            }

            newData = storage.readAddressBook().get();
            model.setAddressBook(newData);
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

            return new CommandResult(generateSuccessMessage());
        } catch (CsvException e) {
            throw new CommandException(MESSAGE_INVALID_VALUE_IN_FILE);
        } catch (IOException e) {
            throw new CommandException(MESSAGE_UNABLE_TO_ACCESS_FILE);
        } catch (DataLoadingException e) {
            throw new CommandException(MESSAGE_CORRUPTED_FILE);
        }
    }

    private boolean isCsvFile(String filePath) {
        return filePath.endsWith(".csv");
    }

    /**
     * Generates a command execution success message based on whether
     * the CSV file is imported
     */
    private String generateSuccessMessage() {
        return String.format(MESSAGE_IMPORT_SUCCESS, filePath);
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

    public Path getFilePath() {
        return filePath;
    }

    public static void setStorage(Storage storage) {
        ImportCommand.storage = storage;
    }
}
