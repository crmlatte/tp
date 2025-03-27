package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.FindCommand;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;
import tassist.address.testutil.PersonBuilder;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validNameArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validStudentIdArgs_returnsFindCommand() throws Exception {
        FindCommand command = parser.parse(VALID_STUDENTID_AMY);
        Person matchingPerson = new PersonBuilder().withStudentId(VALID_STUDENTID_AMY).build();

        Model model = new ModelManager();
        model.addPerson(matchingPerson);
        command.execute(model);

        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(matchingPerson, model.getFilteredPersonList().get(0));
    }
}
