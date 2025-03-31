package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_DUPLICATE_FIELDS;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.ASSIGNMENT_DESC_2103;
import static tassist.address.logic.commands.CommandTestUtil.DATE_DESC_LONG;
import static tassist.address.logic.commands.CommandTestUtil.DATE_DESC_MID;
import static tassist.address.logic.commands.CommandTestUtil.DATE_DESC_SHORT;
import static tassist.address.logic.commands.CommandTestUtil.EXTRA_ARGUMENTS;
import static tassist.address.logic.commands.CommandTestUtil.EXTRA_PREFIX;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_ASSIGNMENT_NAME_EMPTY;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_ASSIGNMENT_NAME_SPACES;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_ASSIGNMENT_NAME_SPECIAL;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_DATE_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_DATE_PAST;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_DATE_VALUES;
import static tassist.address.logic.commands.CommandTestUtil.MULTIPLE_FIELDS_INPUT;
import static tassist.address.logic.commands.CommandTestUtil.MULTIPLE_INVALID_FIELDS_INPUT;
import static tassist.address.logic.commands.CommandTestUtil.MULTIPLE_VALID_FIELDS_INPUT;
import static tassist.address.logic.commands.CommandTestUtil.VALID_ASSIGNMENT_NAME;
import static tassist.address.logic.commands.CommandTestUtil.VALID_DATE_SHORT;
import static tassist.address.logic.parser.AssignmentCommandParser.MESSAGE_DATE_IN_PAST;
import static tassist.address.logic.parser.AssignmentCommandParser.MESSAGE_INVALID_DATE;
import static tassist.address.logic.parser.AssignmentCommandParser.MESSAGE_INVALID_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_DATE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.AssignmentCommand;
import tassist.address.model.timedevents.Assignment;

public class AssignmentCommandParserTest {
    private AssignmentCommandParser parser = new AssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        // Test with full date format (dd-MM-yyyy)
        Assignment expectedAssignment = new Assignment(VALID_ASSIGNMENT_NAME, "",
                LocalDateTime.of(2030, 1, 30, 23, 59));
        assertParseSuccess(parser, ASSIGNMENT_DESC_2103 + DATE_DESC_LONG, new AssignmentCommand(expectedAssignment));

        // Test with short date format (dd-MM-yy)
        expectedAssignment = new Assignment(VALID_ASSIGNMENT_NAME, "",
                LocalDateTime.of(2030, 3, 15, 23, 59));
        System.out.println(ASSIGNMENT_DESC_2103 + DATE_DESC_MID);
        assertParseSuccess(parser, ASSIGNMENT_DESC_2103 + DATE_DESC_MID, new AssignmentCommand(expectedAssignment));

        // Test with minimal date format (dd-MM)
        expectedAssignment = new Assignment(VALID_ASSIGNMENT_NAME, "",
                LocalDateTime.of(LocalDateTime.now().getYear(), 12, 20, 23, 59));
        assertParseSuccess(parser, ASSIGNMENT_DESC_2103 + DATE_DESC_SHORT, new AssignmentCommand(expectedAssignment));
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, "", expectedMessage);

        // missing name prefix
        assertParseFailure(parser, VALID_ASSIGNMENT_NAME + DATE_DESC_SHORT, expectedMessage);

        // missing date prefix
        assertParseFailure(parser, ASSIGNMENT_DESC_2103 + VALID_DATE_SHORT, expectedMessage);

        // missing name value
        assertParseFailure(parser, PREFIX_NAME + DATE_DESC_LONG, expectedMessage);

        // missing date value
        assertParseFailure(parser, ASSIGNMENT_DESC_2103 + PREFIX_DATE, expectedMessage);
    }

    @Test
    public void parse_invalidDate_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // Invalid date format
        assertParseFailure(parser, INVALID_DATE_FORMAT, MESSAGE_INVALID_DATE);

        // Invalid date values
        assertParseFailure(parser, INVALID_DATE_VALUES, MESSAGE_INVALID_DATE);

        // Date in the past
        assertParseFailure(parser, INVALID_DATE_PAST, MESSAGE_DATE_IN_PAST);

        // Today's date
        String today = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertParseFailure(parser, ASSIGNMENT_DESC_2103 + " " + PREFIX_DATE + today, MESSAGE_DATE_IN_PAST);
    }

    @Test
    public void parse_invalidName_failure() {

        // Name starting with special character
        assertParseFailure(parser, INVALID_ASSIGNMENT_NAME_SPECIAL, MESSAGE_INVALID_NAME);

        // Name with only spaces
        assertParseFailure(parser, INVALID_ASSIGNMENT_NAME_SPACES, MESSAGE_INVALID_NAME);

        // Empty name
        assertParseFailure(parser, INVALID_ASSIGNMENT_NAME_EMPTY, MESSAGE_INVALID_NAME);
    }

    @Test
    public void parse_extraArguments_failure() {

        // Extra arguments after valid command
        assertParseFailure(parser, EXTRA_ARGUMENTS, MESSAGE_INVALID_DATE);

        // Extra prefix
        assertParseFailure(parser, EXTRA_PREFIX, MESSAGE_DUPLICATE_FIELDS + PREFIX_NAME);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections
        assertParseFailure(parser, MULTIPLE_FIELDS_INPUT,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_DATE));

        // Multiple valid fields repeated
        assertParseFailure(parser, MULTIPLE_VALID_FIELDS_INPUT,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_DATE));

        // Multiple invalid values
        assertParseFailure(parser, MULTIPLE_INVALID_FIELDS_INPUT,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_DATE));
    }
}
