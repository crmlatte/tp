package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static tassist.address.logic.parser.GithubCommandParser.MESSAGE_MISSING_GITHUB_PREFIX;
import static tassist.address.logic.parser.GithubCommandParser.MESSAGE_REMOVE_GITHUB;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.GithubCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Github;
import tassist.address.model.person.StudentId;

public class GithubCommandParserTest {
    private GithubCommandParser parser = new GithubCommandParser();
    private final String nonEmptyGithub = "https://github.com/default";

    @Test
    public void parse_studentIdSpecified_success() {
        String targetStudentId = VALID_STUDENTID_BOB;
        String userInput = targetStudentId + " " + PREFIX_GITHUB + nonEmptyGithub;
        GithubCommand expectedCommand = new GithubCommand(
                new StudentId(targetStudentId), new Github(nonEmptyGithub));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_indexSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_GITHUB + nonEmptyGithub;
        GithubCommand expectedCommand = new GithubCommand(INDEX_FIRST_PERSON, new Github(nonEmptyGithub));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingCompulsoryField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, GithubCommand.MESSAGE_USAGE);

        // no parameters
        assertParseFailure(parser, GithubCommand.COMMAND_WORD, expectedMessage);

        // no index/studentid
        assertParseFailure(parser, GithubCommand.COMMAND_WORD + " " + nonEmptyGithub, expectedMessage);
    }

    @Test
    public void parse_repeatedGithubValue_failure() {
        String validExpectedCommandString = VALID_STUDENTID_BOB + " " + PREFIX_GITHUB + nonEmptyGithub;

        // multiple github values
        assertParseFailure(parser, validExpectedCommandString + " " + PREFIX_GITHUB + "https://github.com/another",
                getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));

        // invalid value followed by valid value
        assertParseFailure(parser, VALID_STUDENTID_BOB + " " + PREFIX_GITHUB + "invalid "
                        + PREFIX_GITHUB + nonEmptyGithub,
                getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));

        // valid value followed by invalid value
        assertParseFailure(parser, validExpectedCommandString + " " + PREFIX_GITHUB + "invalid",
                getErrorMessageForDuplicatePrefixes(PREFIX_GITHUB));
    }

    @Test
    public void parse_emptyGithubField_returnsGithubCommandWithNoGithubAssigned() throws Exception {
        String input = "1 g/";
        Github expectedGithub = new Github(Github.NO_GITHUB);
        GithubCommand expectedCommand = new GithubCommand(Index.fromOneBased(1), expectedGithub);

        GithubCommand resultCommand = parser.parse(input);

        assertEquals(expectedCommand, resultCommand);
    }

    @Test
    public void parse_missingGithubPrefix_throwsParseException() {
        String input = "1";
        String expectedOutput = MESSAGE_MISSING_GITHUB_PREFIX;

        ParseException thrown = assertThrows(ParseException.class, () ->parser.parse(input));
        assertEquals(expectedOutput, thrown.getMessage());
    }

    @Test
    public void parse_inputNoGithubAssigned_throwsParseException() {
        String input = "1 g/No Github assigned";

        ParseException thrown = assertThrows(ParseException.class, () -> parser.parse(input));
        assertEquals(MESSAGE_REMOVE_GITHUB, thrown.getMessage());
    }
}
