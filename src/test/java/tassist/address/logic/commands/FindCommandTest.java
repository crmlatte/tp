package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_BOB;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tassist.address.testutil.TypicalPersons.ALICE;
import static tassist.address.testutil.TypicalPersons.CARL;
import static tassist.address.testutil.TypicalPersons.ELLE;
import static tassist.address.testutil.TypicalPersons.FIONA;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;
import tassist.address.model.person.StudentId;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstNamePredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate secondNamePredicate =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));

        StudentId firstStudentId = new StudentId(VALID_STUDENTID_AMY);
        StudentId secondStudentId = new StudentId(VALID_STUDENTID_BOB);
        Predicate<Person> firstStudentIdPredicate = person -> person.getStudentId().equals(firstStudentId);
        Predicate<Person> secondStudentIdPredicate = person -> person.getStudentId().equals(secondStudentId);

        FindCommand findFirstNameCommand = new FindCommand(firstNamePredicate);
        FindCommand findSecondNameCommand = new FindCommand(secondNamePredicate);
        FindCommand findFirstStudentIdCommand = new FindCommand(firstStudentIdPredicate);
        FindCommand findSecondStudentIdCommand = new FindCommand(secondStudentIdPredicate);

        // same object -> returns true
        assertTrue(findFirstNameCommand.equals(findFirstNameCommand));
        assertTrue(findFirstStudentIdCommand.equals(findFirstStudentIdCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstNamePredicate);
        assertTrue(findFirstNameCommand.equals(findFirstCommandCopy));
        FindCommand findFirstStudentIdCommandCopy = new FindCommand(firstStudentIdPredicate);
        assertTrue(findFirstStudentIdCommand.equals(findFirstStudentIdCommandCopy));

        // different types -> returns false
        assertFalse(findFirstNameCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstNameCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstNameCommand.equals(findSecondNameCommand));
        assertFalse(findFirstStudentIdCommand.equals(findSecondStudentIdCommand));
        assertFalse(findFirstNameCommand.equals(findFirstStudentIdCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = prepareNameKeywordsPredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_studentId_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        StudentId studentId = new StudentId("A1111111B");
        Predicate<Person> predicate = person -> person.getStudentId().equals(studentId);
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_studentId_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        StudentId studentId = new StudentId("A9999999Z");
        Predicate<Person> predicate = person -> person.getStudentId().equals(studentId);
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = prepareNameKeywordsPredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialNameMatch_personFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate predicate = prepareNameKeywordsPredicate("Pau");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialNameMatch_caseInsensitive() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        NameContainsKeywordsPredicate predicate = prepareNameKeywordsPredicate("ali");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_partialNameMatch_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = prepareNameKeywordsPredicate("xyz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void toString_withNamePredicate_returnsCorrectFormat() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{namePredicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    @Test
    public void toString_withStudentIdPredicate_returnsCorrectFormat() {
        Predicate<Person> studentIdPredicate = person -> person.getStudentId().value.equals("A1234567X");
        FindCommand findCommand = new FindCommand(studentIdPredicate);
        String expected = FindCommand.class.getCanonicalName() + "{studentIdPredicate=" + studentIdPredicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate prepareNameKeywordsPredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

}
