package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.ProgressCommand.COMMAND_WORD;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static tassist.address.model.person.Progress.MESSAGE_CONSTRAINTS;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.ProgressCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;

public class ProgressCommandParserTest {
    private Progress progress = new Progress("70");
    private ProgressCommandParser parser = new ProgressCommandParser();
    @Test
    public void parse_studentIdSpecified_success() {
        String targetStudentId = VALID_STUDENTID_BOB;
        String userInput = targetStudentId + " " + PREFIX_PROGRESS + "70";
        ProgressCommand expectedCommand = new ProgressCommand(
                new StudentId(targetStudentId), progress);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_indexSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_PROGRESS + "70";
        ProgressCommand expectedCommand = new ProgressCommand(INDEX_FIRST_PERSON, progress);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingAllField_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProgressCommand.MESSAGE_USAGE);

        // no parameters
        ParseException thrown = assertThrows(ParseException.class, () -> parser.parse(""));
        assertParseFailure(parser, COMMAND_WORD, expectedMessage);
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    public void parse_missingPreamble_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ProgressCommand.MESSAGE_USAGE);

        // no index/studentid
        String input = " pr/40";
        ParseException thrown = assertThrows(ParseException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, thrown.getMessage());
        assertParseFailure(parser, COMMAND_WORD + " " + "pr/40", expectedMessage);
    }

    @Test
    public void parse_nonIntegerProgressInput_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_CONSTRAINTS);

        // no index/studentid
        String input = " 1 pr/nonInteger";
        ParseException thrown = assertThrows(ParseException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, thrown.getMessage());
        assertParseFailure(parser, COMMAND_WORD + input, expectedMessage);
    }
}
