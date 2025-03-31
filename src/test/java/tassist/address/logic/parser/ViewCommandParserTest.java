package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.ViewCommand;
import tassist.address.logic.parser.exceptions.ParseException;

public class ViewCommandParserTest {
    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_emptyArgs_returnsViewCommand() throws ParseException {
        ViewCommand expectedViewCommand = new ViewCommand();
        assertTrue(parser.parse("") instanceof ViewCommand);
        assertTrue(parser.parse("   ") instanceof ViewCommand);
    }

    @Test
    public void parse_nonEmptyArgs_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewCommand.COMMAND_WORD), () -> parser.parse("some arguments"));
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewCommand.COMMAND_WORD), () -> parser.parse("1"));
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewCommand.COMMAND_WORD), () -> parser.parse("view"));
    }
}
