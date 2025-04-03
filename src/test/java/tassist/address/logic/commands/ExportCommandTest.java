package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;

public class ExportCommandTest {

    @TempDir
    public static Path temporaryFolder;

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "CsvJsonConverterTest");

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validAbsolutePath_success() throws CommandException {
        Path testOutputCsvFilePath = TEST_DATA_FOLDER.resolve("valid.csv");
        ExportCommand exportCommand = new ExportCommand(testOutputCsvFilePath);

        CommandResult result = exportCommand.execute(model);
        assertEquals(String.format(ExportCommand.MESSAGE_EXPORT_SUCCESS, testOutputCsvFilePath),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_nullFilePath_throwsCommandException() {
        assertThrows(CommandException.class, () -> new ExportCommand(null).execute(model));
    }

    @Test
    public void execute_nonCsvFileType_throwsCommandException() {
        Path nonCsvFilePath = TEST_DATA_FOLDER.resolve("nonCsv.txt");
        assertThrows(CommandException.class, () -> new ExportCommand(nonCsvFilePath).execute(model));
    }

    @Test
    public void equals() {
        Path filePath = temporaryFolder.resolve("output-1.csv");
        final ExportCommand standardCommand = new ExportCommand(filePath);

        // same values -> returns true
        ExportCommand commandWithSameValues = new ExportCommand(temporaryFolder.resolve("output-1.csv"));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different filePath -> returns false
        assertFalse(standardCommand.equals(new ExportCommand(temporaryFolder.resolve("output-2.csv"))));
    }
}
