package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.AssignCommand;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.StudentId;

public class AssignCommandParserTest {
    private AssignCommandParser parser = new AssignCommandParser();

    @Test
    public void parse_validArgsWithIndex_returnsAssignCommand() {
        assertParseSuccess(parser, "1 2", new AssignCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_validArgsWithStudentId_returnsAssignCommand() {
        assertParseSuccess(parser, "1 A1234567M", new AssignCommand(INDEX_FIRST_PERSON, new StudentId("A1234567M")));
    }

    @Test
    public void parse_validArgsWithClassNumber_returnsAssignCommand() {
        assertParseSuccess(parser, "1 T01", new AssignCommand(INDEX_FIRST_PERSON, new ClassNumber("T01")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // empty argument
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // single argument
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid timed event index
        assertParseFailure(parser, "a 2", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid student ID
        assertParseFailure(parser, "1 A123456",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid class number
        assertParseFailure(parser, "1 T00", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid student index
        assertParseFailure(parser, "1 a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // too many arguments
        assertParseFailure(parser, "1 2 3", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));
    }
}
