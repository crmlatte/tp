package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.ABSOLUTE_FILE_PATH_UNIX;
import static tassist.address.logic.commands.CommandTestUtil.ABSOLUTE_FILE_PATH_WINDOWS;
import static tassist.address.logic.commands.CommandTestUtil.RELATIVE_FILE_PATH_UNIX;
import static tassist.address.logic.commands.CommandTestUtil.RELATIVE_FILE_PATH_WINDOWS;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.ImportCommand;

public class ImportCommandParserTest {
    private ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_relativePath_throwsParseException() {
        final String relativeFilePath = System.getProperty("os.name").toLowerCase().contains("win")
                ? RELATIVE_FILE_PATH_WINDOWS
                : RELATIVE_FILE_PATH_UNIX;

        assertParseFailure(parser, relativeFilePath,
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_absolutePath_returnsImportCommand() {
        final String absoluteFilePath = System.getProperty("os.name").toLowerCase().contains("win")
                ? ABSOLUTE_FILE_PATH_WINDOWS
                : ABSOLUTE_FILE_PATH_UNIX;

        assertParseSuccess(parser, absoluteFilePath, new ImportCommand(Paths.get(absoluteFilePath)));
    }
}
