package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_CLASS_BOB;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.commands.ClassCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.StudentId;

public class ClassCommandParserTest {
    private ClassCommandParser parser = new ClassCommandParser();
    private final String nonEmptyClass = "T01";

    @Test
    public void parse_indexSpecified_success() {
        // have Class
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_CLASS + nonEmptyClass;
        ClassCommand expectedCommand = new ClassCommand(INDEX_FIRST_PERSON, new ClassNumber(nonEmptyClass));
        assertParseSuccess(parser, userInput, expectedCommand);

        // no Class
        userInput = targetIndex.getOneBased() + " " + PREFIX_CLASS;
        expectedCommand = new ClassCommand(INDEX_FIRST_PERSON, new ClassNumber(ClassNumber.DEFAULT_CLASS));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validStudentId_success() throws Exception {
        String userInput = VALID_STUDENTID_AMY + " " + PREFIX_CLASS + VALID_CLASS_BOB;
        ClassCommand expectedCommand = new ClassCommand(new StudentId(VALID_STUDENTID_AMY),
                new ClassNumber(VALID_CLASS_BOB));
        assertEquals(expectedCommand, parser.parse(userInput));
    }

    @Test
    public void parse_invalidStudentIdFormat_throwsParseException() {
        String userInput = "INVALID_ID " + PREFIX_CLASS + VALID_CLASS_BOB;
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_missingPreamble_throwsParseException() {
        String userInput = " " + PREFIX_CLASS + VALID_CLASS_AMY;
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_invalidClassNumberFormat_throwsParseException() {
        String userInput = VALID_STUDENTID_AMY + " " + PREFIX_CLASS + "S200"; // Invalid class number
        assertThrows(ParseException.class, () -> parser.parse(userInput));
    }

    @Test
    public void parse_blankClassNumber_assignsDefault() throws Exception {
        String userInput = VALID_STUDENTID_AMY + " " + PREFIX_CLASS;
        ClassCommand classCommand = new ClassCommand(new StudentId(VALID_STUDENTID_AMY),
                new ClassNumber(ClassNumber.DEFAULT_CLASS));
        assertEquals(classCommand, parser.parse(userInput));
    }
}
