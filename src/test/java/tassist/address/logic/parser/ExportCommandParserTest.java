package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.VALID_FILE_PATH_1;
import static tassist.address.logic.commands.CommandTestUtil.VALID_FILE_PATH_2;
import static tassist.address.logic.commands.ExportCommand.MESSAGE_EXPORT_FAILURE;
import static tassist.address.logic.commands.ExportCommand.MESSAGE_PARENT_FOLDER_DOES_NOT_EXIST;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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

        assertParseFailure(parser, relativeFilePath, MESSAGE_INVALID_FILE_PATH);
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
        FileSystem fileSystem = FileSystems.getDefault();
        Path rootDirectory = fileSystem.getRootDirectories().iterator().next();

        assertParseFailure(parser, rootDirectory.toString(), MESSAGE_INVALID_FILE_PATH);
    }

    @Test
    public void execute_nonExistentParentFolder_throwsCommandException() {
        // creates a path, but does not create any missing directories or files
        Path nonExistentParentFolderPath =
                testRoot.resolve("nonExistentParentFolder").resolve("sample.csv");
        assertParseFailure(parser, nonExistentParentFolderPath.toString(),
                MESSAGE_EXPORT_FAILURE + "\n" + MESSAGE_PARENT_FOLDER_DOES_NOT_EXIST);
    }
}
