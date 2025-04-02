package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.VALID_FILE_PATH_1;
import static tassist.address.logic.commands.CommandTestUtil.VALID_FILE_PATH_2;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.commands.ExportCommand;

public class ExportCommandParserTest {

    @TempDir
    public Path testRoot;
    private ExportCommandParser parser = new ExportCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_relativePath_throwsParseException() {
        final String relativeFilePath = VALID_FILE_PATH_1;

        assertParseFailure(parser, relativeFilePath,
                String.format(MESSAGE_INVALID_FILE_PATH, ExportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validAbsolutePath_returnsExportCommand() {
        // mimics absolute path
        final Path absoluteOutputFilePath = testRoot.resolve(VALID_FILE_PATH_2);

        String userInput = absoluteOutputFilePath.toString();
        ExportCommand expectedCommand = new ExportCommand(Paths.get(absoluteOutputFilePath.toString()));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_rootDirectory_throwsParseException() {
        // root directory
        final String rootDirectory = "/";

        assertParseFailure(parser, rootDirectory,
                String.format(MESSAGE_INVALID_FILE_PATH, ExportCommand.MESSAGE_USAGE));
    }
}
