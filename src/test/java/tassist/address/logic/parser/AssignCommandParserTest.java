package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_CLASS_NUMBER;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_INDEX;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_STUDENT_ID;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_STUDENT_INDEX;
import static tassist.address.logic.commands.CommandTestUtil.TOO_MANY_ARGUMENTS;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_NUMBER;
import static tassist.address.logic.commands.CommandTestUtil.VALID_INDEX_ONE;
import static tassist.address.logic.commands.CommandTestUtil.VALID_INDEX_TWO;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENT_ID;
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
        assertParseSuccess(parser, VALID_INDEX_ONE + " " + VALID_INDEX_TWO,
                new AssignCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
    }

    @Test
    public void parse_validArgsWithStudentId_returnsAssignCommand() {
        assertParseSuccess(parser, VALID_INDEX_ONE + " " + VALID_STUDENT_ID,
                new AssignCommand(INDEX_FIRST_PERSON, new StudentId(VALID_STUDENT_ID)));
    }

    @Test
    public void parse_validArgsWithClassNumber_returnsAssignCommand() {
        assertParseSuccess(parser, VALID_INDEX_ONE + " " + VALID_CLASS_NUMBER,
                new AssignCommand(INDEX_FIRST_PERSON, new ClassNumber(VALID_CLASS_NUMBER)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // empty argument
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // single argument
        assertParseFailure(parser, VALID_INDEX_ONE, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AssignCommand.MESSAGE_USAGE));

        // invalid timed event index
        assertParseFailure(parser, INVALID_INDEX + " " + VALID_INDEX_TWO,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid student ID
        assertParseFailure(parser, VALID_INDEX_ONE + " " + INVALID_STUDENT_ID,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid class number
        assertParseFailure(parser, VALID_INDEX_ONE + " " + INVALID_CLASS_NUMBER,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid student index
        assertParseFailure(parser, VALID_INDEX_ONE + " " + INVALID_STUDENT_INDEX,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // too many arguments
        assertParseFailure(parser, TOO_MANY_ARGUMENTS,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));
    }
}
