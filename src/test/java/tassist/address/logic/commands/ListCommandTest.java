package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static tassist.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_sortByNameAscending_success() throws Exception {
        ListCommand command = new ListCommand("name", "asc", null, null);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        List<Person> sortedExpectedList = new ArrayList<>(expectedModel.getFilteredPersonList());
        sortedExpectedList.sort(Comparator.comparing(p -> p.getName().fullName));

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);

        List<Person> actualList = model.getFilteredPersonList();
        assertEquals(sortedExpectedList, actualList);
    }

    @Test
    public void execute_sortByProgressDescending_success() throws Exception {
        ListCommand command = new ListCommand("progress", "des", null, null);
        expectedModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        List<Person> sortedList = new ArrayList<>(expectedModel.getFilteredPersonList());
        sortedList.sort(Comparator.comparing(p -> p.getProgress().value, Comparator.reverseOrder()));

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);

        List<Person> actualList = new ArrayList<>(model.getFilteredPersonList());
        assertEquals(sortedList, actualList);
    }

    @Test
    public void execute_filterByProgress_success() throws Exception {
        ListCommand command = new ListCommand(null, null, "progress", "30");

        Predicate<Person> expectedPredicate = person -> person.getProgress().value <= 30;
        expectedModel.updateFilteredPersonList(expectedPredicate);

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_filterByProgress_noMatch() throws Exception {
        ListCommand command = new ListCommand(null, null, "progress", "-1"); // Assume no one has 999%

        expectedModel.updateFilteredPersonList(person -> person.getProgress().value <= -1);

        assertCommandSuccess(command, model, ListCommand.MESSAGE_NO_STUDENTS, expectedModel);
    }

    @Test
    public void execute_filterProgressAndSortNameDesc_success() throws Exception {
        ListCommand command = new ListCommand("name", "des", "progress", "30");

        Predicate<Person> expectedPredicate = person -> person.getProgress().value <= 30;
        expectedModel.updateFilteredPersonList(expectedPredicate);
        List<Person> sortedList = new ArrayList<>(expectedModel.getFilteredPersonList());
        sortedList.sort(Comparator.comparing(p -> p.getProgress().value, Comparator.reverseOrder()));

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /*@Test
    public void execute_filterByTeam_success() throws Exception {
        ListCommand command = new ListCommand(null, null, "team", "alpha");

        Predicate<Person> expectedPredicate = person -> person.getTeam().equalsIgnoreCase("alpha");
        expectedModel.updateFilteredPersonList(expectedPredicate);

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }*/

}
