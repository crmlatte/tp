package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.VALID_FILE_PATH;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.commands.ImportCommand;
import tassist.address.logic.commands.ImportCommandTest;
import tassist.address.storage.JsonAddressBookStorage;
import tassist.address.storage.JsonUserPrefsStorage;
import tassist.address.storage.Storage;

public class ImportCommandParserTest {

    @TempDir
    public Path testRoot;
    private ImportCommandParser parser = new ImportCommandParser();
    private Storage storage;
    private JsonAddressBookStorage addressBookStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    @BeforeEach
    public void setUp() throws IOException {
        // simulate the addressBook.json file
        Path addressBookFile = testRoot.resolve("addressBook.json");
        if (!Files.exists(addressBookFile)) {
            Files.createFile(addressBookFile);
        }
        addressBookStorage = new JsonAddressBookStorage(addressBookFile);
        userPrefsStorage = new JsonUserPrefsStorage(testRoot.resolve("userPrefs.json"));
        storage = new ImportCommandTest.TestStorageManager(addressBookStorage, userPrefsStorage);
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_relativePath_throwsParseException() {
        final String relativeFilePath = VALID_FILE_PATH;

        assertParseFailure(parser, relativeFilePath,
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validAbsolutePath_returnsImportCommand() throws IOException {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(VALID_FILE_PATH);

        // creates the file so it "exists"
        if (!Files.exists(absoluteFilePath)) {
            Files.createFile(absoluteFilePath);
        }

        String userInput = absoluteFilePath.toString();
        ImportCommand expectedCommand = new ImportCommand(Paths.get(absoluteFilePath.toString()));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidAbsolutePath_returnsImportCommand() throws IOException {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(INVALID_FILE_PATH);

        // does not create the file so it "doesn't exist"

        assertParseFailure(parser, absoluteFilePath.toString(),
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }
}
