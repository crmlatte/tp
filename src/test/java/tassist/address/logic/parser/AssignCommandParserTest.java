package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_NUMBER;
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
    public void parse_validArgs_returnsAssignCommand() {
        // no leading and trailing whitespaces
        AssignCommand expectedAssignCommand =
                new AssignCommand(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON);
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + " " + INDEX_SECOND_PERSON.getOneBased(),
                expectedAssignCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n " + INDEX_FIRST_PERSON.getOneBased() + " \n \t "
                + INDEX_SECOND_PERSON.getOneBased() + " \t", expectedAssignCommand);
    }

    @Test
    public void parse_validArgsWithStudentId_returnsAssignCommand() {
        AssignCommand expectedAssignCommand =
                new AssignCommand(INDEX_SECOND_PERSON, new StudentId(VALID_STUDENT_ID));
        assertParseSuccess(parser, VALID_STUDENT_ID + " " + INDEX_SECOND_PERSON.getOneBased(),
                expectedAssignCommand);
    }

    @Test
    public void parse_validArgsWithClassNumber_returnsAssignCommand() {
        AssignCommand expectedAssignCommand =
                new AssignCommand(INDEX_SECOND_PERSON, new ClassNumber(VALID_CLASS_NUMBER));
        assertParseSuccess(parser, VALID_CLASS_NUMBER + " " + INDEX_SECOND_PERSON.getOneBased(),
                expectedAssignCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // invalid student index
        assertParseFailure(parser, "a " + INDEX_SECOND_PERSON.getOneBased(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid timed event index
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid student ID
        assertParseFailure(parser, "A1234567 " + INDEX_SECOND_PERSON.getOneBased(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // invalid class number
        assertParseFailure(parser, "T1 " + INDEX_SECOND_PERSON.getOneBased(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // missing student identifier
        assertParseFailure(parser, " " + INDEX_SECOND_PERSON.getOneBased(),
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));

        // missing timed event index
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE));
    }
}
