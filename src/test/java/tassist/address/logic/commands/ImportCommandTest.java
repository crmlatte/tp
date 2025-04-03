package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.commons.core.GuiSettings;
import tassist.address.commons.exceptions.DataLoadingException;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.ReadOnlyUserPrefs;
import tassist.address.model.UserPrefs;
import tassist.address.storage.AddressBookStorage;
import tassist.address.storage.JsonAddressBookStorage;
import tassist.address.storage.JsonUserPrefsStorage;
import tassist.address.storage.Storage;
import tassist.address.storage.StorageManager;
import tassist.address.storage.UserPrefsStorage;

public class ImportCommandTest {

    @TempDir
    public static Path temporaryFolder;

    private Model model;
    private Storage storage;
    private JsonAddressBookStorage addressBookStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    @BeforeEach
    public void setUp() throws IOException {
        // simulate the addressBook.json file
        Path addressBookFilePath = temporaryFolder.resolve("addressBook.json");
        if (!Files.exists(addressBookFilePath)) {
            Files.createFile(addressBookFilePath);
        }
        addressBookStorage = new JsonAddressBookStorage(addressBookFilePath);
        userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        storage = new StorageManager(addressBookStorage, userPrefsStorage);
        model = new ModelManager(getTypicalAddressBook(), new TestUserPrefs(addressBookFilePath));
        ImportCommand.setStorage(storage);
    }

    /*@Test
    public void execute_validAbsolutePath_success() throws CommandException {
        Path testCsvFilePath = Paths.get("src", "test", "data",
                "CsvJsonConverterTest", "valid.csv");
        ImportCommand importCommand = new ImportCommand(testCsvFilePath);

        CommandResult result = importCommand.execute(model);
        assertEquals(testCsvFilePath, importCommand.getFilePath());
        assertEquals(String.format(ImportCommand.MESSAGE_IMPORT_SUCCESS, testCsvFilePath),
                result.getFeedbackToUser());
    }*/

    @Test
    public void execute_nullFilePath_throwsCommandException() {
        assertThrows(CommandException.class, () -> new ImportCommand(null).execute(model));
    }

    @Test
    public void execute_nonCsvFileType_throwsCommandException() {
        Path nonCsvFilePath = Paths.get("src", "test", "data",
                "CsvJsonConverterTest", "nonCsv.txt");
        assertThrows(CommandException.class, () -> new ImportCommand(nonCsvFilePath).execute(model));
    }

    @Test
    public void execute_invalidValueCsv_throwsCommandException() {
        Path invalidValueCsvFilePath = Paths.get("src", "test", "data",
                "CsvJsonConverterTest", "invalidValue.csv");
        assertThrows(CommandException.class, () -> new ImportCommand(invalidValueCsvFilePath).execute(model));
    }

    @Test
    public void execute_nonExistentFilePath_throwsCommandException() {
        Path nonExistentFilePath = Paths.get("src", "test", "data",
                "CsvJsonConverterTest", "nonExistent.csv");
        assertThrows(CommandException.class, () -> new ImportCommand(nonExistentFilePath).execute(model));
    }

    @Test
    public void execute_corruptedJsonFile_throwsCommandException() {
        Model testModel = new ModelManager(getTypicalAddressBook(),
                new TestUserPrefs(temporaryFolder.resolve("random.csv")));
        Path testCsvFilePath = Paths.get("src", "test", "data",
                "CsvJsonConverterTest", "valid.csv");
        assertThrows(CommandException.class, () -> new ImportCommand(testCsvFilePath).execute(testModel));
    }

    @Test
    public void equals() {
        Path filePath = temporaryFolder.resolve("test1.csv");
        final ImportCommand standardCommand = new ImportCommand(filePath);

        // same values -> returns true
        ImportCommand commandWithSameValues = new ImportCommand(temporaryFolder.resolve("test1.csv"));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different filePath -> returns false
        assertFalse(standardCommand.equals(new ImportCommand(temporaryFolder.resolve("test2.csv"))));
    }

    /**
     * Test implementation of StorageManager that uses the temporary folder.
     */
    public static class TestStorageManager implements Storage {
        private AddressBookStorage addressBookStorage;
        private UserPrefsStorage userPrefsStorage;

        /**
         * Creates a {@code StorageManager} with the given {@code AddressBookStorage} and {@code UserPrefStorage}.
         */
        public TestStorageManager(AddressBookStorage addressBookStorage, UserPrefsStorage userPrefsStorage) {
            this.addressBookStorage = addressBookStorage;
            this.userPrefsStorage = userPrefsStorage;
        }

        @Override
        public Path getUserPrefsFilePath() {
            return null;
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
            return Optional.empty();
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            // do nothing
        }

        @Override
        public Path getAddressBookFilePath() {
            return null;
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
            return readAddressBook(addressBookStorage.getAddressBookFilePath());
        }

        @Override
        public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
            return addressBookStorage.readAddressBook(filePath);
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            // do nothing
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
            // do nothing
        }
    }

    /**
     * Test implementation of ReadOnlyUserPrefs that holds the temporary folder addressBookFilePath.
     */
    public static class TestUserPrefs implements ReadOnlyUserPrefs {
        private GuiSettings guiSettings = new GuiSettings();
        private Path addressBookFilePath;

        public TestUserPrefs(Path addressBookFilePath) {
            this.addressBookFilePath = addressBookFilePath;
        }

        /**
         * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
         */
        public void resetData(ReadOnlyUserPrefs newUserPrefs) {
            requireNonNull(newUserPrefs);
            setGuiSettings(newUserPrefs.getGuiSettings());
            setAddressBookFilePath(newUserPrefs.getAddressBookFilePath());
        }

        public GuiSettings getGuiSettings() {
            return guiSettings;
        }

        public void setGuiSettings(GuiSettings guiSettings) {
            // do nothing
        }

        public Path getAddressBookFilePath() {
            return addressBookFilePath;
        }

        public void setAddressBookFilePath(Path addressBookFilePath) {
            // do nothing
        }

        @Override
        public boolean equals(Object other) {
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return null;
        }
    }
}
