package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_INVALID_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.ABSOLUTE_FILE_PATH;
import static tassist.address.logic.commands.CommandTestUtil.RELATIVE_FILE_PATH;
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
        assertParseFailure(parser, RELATIVE_FILE_PATH,
                String.format(MESSAGE_INVALID_FILE_PATH, ImportCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_absolutePath_returnsImportCommand() {
        assertParseSuccess(parser, ABSOLUTE_FILE_PATH, new ImportCommand(Paths.get(ABSOLUTE_FILE_PATH)));
    }
}
