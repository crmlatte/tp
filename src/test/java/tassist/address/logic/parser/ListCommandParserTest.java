package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tassist.address.logic.commands.ListCommand.MESSAGE_MISSING_FILTER_VALUE;
import static tassist.address.logic.commands.ListCommand.MESSAGE_MISSING_SORT_ORDER;
import static tassist.address.logic.parser.ListCommandParser.MESSAGE_MISSING_FILTER_TYPE;
import static tassist.address.logic.parser.ListCommandParser.MESSAGE_MISSING_SORT_TYPE;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {
    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_validArgs_returnsListCommand() throws Exception {
        // No arguments -> should return a ListCommand with null parameters
        ListCommand expectedCommand1 = new ListCommand(null, null, null, null);
        assertEquals(expectedCommand1, parser.parse(""));

        // Sort by name ascending
        ListCommand expectedCommand2 = new ListCommand("name", "asc", null, null);
        assertEquals(expectedCommand2, parser.parse(" s/name o/asc"));

        // Sort by progress descending
        ListCommand expectedCommand3 = new ListCommand("progress", "des", null, null);
        assertEquals(expectedCommand3, parser.parse(" s/progress o/des"));

        // Filter by class T01
        ListCommand expectedCommand4 = new ListCommand(null, null, "class", "T01");
        assertEquals(expectedCommand4, parser.parse(" f/class fv/T01"));

        // Sort by GitHub ascending, filter by team Alpha
        ListCommand expectedCommand5 = new ListCommand("github", "asc", "team", "Alpha");
        assertEquals(expectedCommand5, parser.parse(" s/github o/asc f/team fv/Alpha"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Invalid argument
        assertThrows(ParseException.class, () -> parser.parse(" abcdefg"));
        // Invalid sort type
        assertThrows(ParseException.class, () -> parser.parse(" s/invalid o/asc"));

        // Invalid sort order
        assertThrows(ParseException.class, () -> parser.parse(" s/name o/random"));

        // Invalid filter type
        assertThrows(ParseException.class, () -> parser.parse(" f/invalid fv/CS2103"));
    }

    @Test
    public void parse_invalidProgressNonNumber_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" f/progress fv/notANumber"));
    }

    @Test
    public void parse_invalidProgressOutOfBounds_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" f/progress fv/150"));
    }

    @Test
    public void parse_missingSortOrder_throwsParseException() {
        // Missing sort order
        Exception thrown = assertThrows(ParseException.class, () -> parser.parse(" s/name"));
        assertEquals(MESSAGE_MISSING_SORT_ORDER, thrown.getMessage());

        Exception thrown2 = assertThrows(ParseException.class, () -> parser.parse(" f/class fv/T01 s/name"));
        assertEquals(MESSAGE_MISSING_SORT_ORDER, thrown2.getMessage());
    }

    @Test
    public void parse_missingSortType_throwsParseException() {
        //Missing sort type
        Exception thrown = assertThrows(ParseException.class, () -> parser.parse(" o/asc"));
        assertEquals(MESSAGE_MISSING_SORT_TYPE, thrown.getMessage());

        Exception thrown2 = assertThrows(ParseException.class, () -> parser.parse(" f/class fv/T01 o/asc"));
        assertEquals(MESSAGE_MISSING_SORT_TYPE, thrown2.getMessage());
    }

    @Test
    public void parse_missingFilterValue_throwsParseException() {
        // Missing filter value
        Exception thrown = assertThrows(ParseException.class, () -> parser.parse(" f/class"));
        assertEquals(MESSAGE_MISSING_FILTER_VALUE, thrown.getMessage());

        Exception thrown2 = assertThrows(ParseException.class, () -> parser.parse(" f/class s/name o/asc"));
        assertEquals(MESSAGE_MISSING_FILTER_VALUE, thrown2.getMessage());
    }

    @Test
    public void parse_missingFilterType_throwsParseException() {
        //Missing filter type
        Exception thrown = assertThrows(ParseException.class, () -> parser.parse(" fv/60"));
        assertEquals(MESSAGE_MISSING_FILTER_TYPE, thrown.getMessage());

        Exception thrown2 = assertThrows(ParseException.class, () -> parser.parse(" fv/60 s/name o/des"));
        assertEquals(MESSAGE_MISSING_FILTER_TYPE, thrown2.getMessage());
    }

}
