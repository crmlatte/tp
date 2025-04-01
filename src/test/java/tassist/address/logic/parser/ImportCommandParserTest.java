package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.FILE_PATH_1;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.ImportCommand;

public class ImportCommandParserTest {

    @TempDir
    public Path testRoot;
    private ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_relativePath_throwsParseException() {
        final String relativeFilePath = FILE_PATH_1;

        assertParseFailure(parser, relativeFilePath,
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validAbsolutePath_returnsImportCommand() throws IOException {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(FILE_PATH_1);

        // creates the file so it "exists"
        Files.createFile(absoluteFilePath);

        assertParseSuccess(parser, absoluteFilePath.toString(),
                new ImportCommand(Paths.get(absoluteFilePath.toString())));
    }

    @Test
    public void parse_invalidAbsolutePath_returnsImportCommand() throws IOException {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve(FILE_PATH_1);

        // does not create the file so it "doesn't exist"

        assertParseFailure(parser, absoluteFilePath.toString(),
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }
}
