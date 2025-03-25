package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.AssignmentCommand;
import tassist.address.model.timedevents.Assignment;

public class AssignmentCommandParserTest {
    private AssignmentCommandParser parser = new AssignmentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        // Test with full date format (dd-MM-yyyy)
        String userInput = " n/CS2103T Project d/30-01-2030";
        Assignment expectedAssignment = new Assignment("CS2103T Project", "",
                LocalDateTime.of(2030, 1, 30, 23, 59));
        assertParseSuccess(parser, userInput, new AssignmentCommand(expectedAssignment));

        // Test with short date format (dd-MM-yy)
        userInput = " n/CS2103T Quiz d/15-03-30";
        expectedAssignment = new Assignment("CS2103T Quiz", "",
                LocalDateTime.of(2030, 3, 15, 23, 59));
        assertParseSuccess(parser, userInput, new AssignmentCommand(expectedAssignment));

        // Test with minimal date format (dd-MM)
        userInput = " n/CS2103T Exam d/20-04";
        expectedAssignment = new Assignment("CS2103T Exam", "",
                LocalDateTime.of(LocalDateTime.now().getYear(), 4, 20, 23, 59));
        assertParseSuccess(parser, userInput, new AssignmentCommand(expectedAssignment));
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, "", expectedMessage);

        // missing name prefix
        assertParseFailure(parser, "CS2103T Project d/30-01-2030", expectedMessage);

        // missing date prefix
        assertParseFailure(parser, "n/CS2103T Project 30-01-2030", expectedMessage);

        // missing name value
        assertParseFailure(parser, "n/ d/30-01-2030", expectedMessage);

        // missing date value
        assertParseFailure(parser, "n/CS2103T Project d/", expectedMessage);
    }

    @Test
    public void parse_invalidDate_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // Invalid date format
        assertParseFailure(parser, "n/CS2103T Project d/2030-01-30", expectedMessage);

        // Invalid date values
        assertParseFailure(parser, "n/CS2103T Project d/32-01-2030", expectedMessage);

        // Date in the past
        assertParseFailure(parser, "n/CS2103T Project d/01-01-2020", expectedMessage);

        // Today's date
        String today = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertParseFailure(parser, "n/CS2103T Project d/" + today, expectedMessage);
    }

    @Test
    public void parse_invalidName_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // Name starting with special character
        assertParseFailure(parser, "n/@CS2103T Project d/30-01-2030", expectedMessage);

        // Name with only spaces
        assertParseFailure(parser, "n/   d/30-01-2030", expectedMessage);

        // Empty name
        assertParseFailure(parser, "n/d/30-01-2030", expectedMessage);
    }

    @Test
    public void parse_extraArguments_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignmentCommand.MESSAGE_USAGE);

        // Extra arguments after valid command
        assertParseFailure(parser, "n/CS2103T Project d/30-01-2030 extra", expectedMessage);

        // Extra prefix
        assertParseFailure(parser, "n/CS2103T Project d/30-01-2030 n/Extra", expectedMessage);
    }
}
