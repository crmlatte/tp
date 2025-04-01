package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_REPOSITORY_NAME;
import static tassist.address.logic.commands.CommandTestUtil.INVALID_USERNAME;
import static tassist.address.logic.commands.CommandTestUtil.VALID_REPOSITORY_NAME;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_USERNAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_USERNAME;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.RepoCommand;
import tassist.address.model.person.StudentId;

public class RepoCommandParserTest {

    private final RepoCommandParser parser = new RepoCommandParser();

    private final String usernamePrefix = " un/" + VALID_USERNAME;
    private final String repoPrefix = " rn/" + VALID_REPOSITORY_NAME;

    @Test
    public void parse_indexSpecified_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + usernamePrefix + repoPrefix;
        RepoCommand expectedCommand = new RepoCommand(INDEX_FIRST_PERSON, VALID_USERNAME, VALID_REPOSITORY_NAME);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_studentIdSpecified_success() {
        String userInput = VALID_STUDENTID_BOB + usernamePrefix + repoPrefix;
        RepoCommand expectedCommand = new RepoCommand(new StudentId(VALID_STUDENTID_BOB), VALID_USERNAME,
                VALID_REPOSITORY_NAME);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingUsername_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " rn/" + VALID_REPOSITORY_NAME;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_missingRepositoryName_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " un/" + VALID_USERNAME;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_invalidUsername_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " un/" + INVALID_USERNAME + " rn/" + VALID_REPOSITORY_NAME ;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_INVALID_USERNAME);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_invalidRepositoryName_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased()
                + " un/" + VALID_USERNAME + " rn/" + INVALID_REPOSITORY_NAME;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RepoCommand.MESSAGE_INVALID_REPOSITORY_NAME);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_missingBothPrefixes_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " ";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_emptyArgs_failure() {
        String userInput = " ";
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_invalidIndex_failure() {
        String userInput = "zero un/" + VALID_USERNAME + " rn/" + VALID_REPOSITORY_NAME;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_invalidStudentId_failure() {
        String userInput = "notAnId un/" + VALID_USERNAME + " rn/" + VALID_REPOSITORY_NAME;
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_duplicateUsernamePrefix_failure() {
        String userInput = "1 un/ValidUser un/AnotherUser rn/valid-repo";
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_USERNAME);
        assertParseFailure(parser, userInput, expectedMessage);
    }

    @Test
    public void parse_duplicateRepositoryPrefix_failure() {
        String userInput = "1 un/ValidUser rn/valid-repo rn/another-repo";
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REPOSITORY_NAME);
        assertParseFailure(parser, userInput, expectedMessage);
    }
}
