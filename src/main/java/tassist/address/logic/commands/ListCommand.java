package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.person.Person;

/**
 * Lists all students in the TAssist system, with optional sorting and filtering.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_SUCCESS = "Listed all students";
    public static final String MESSAGE_NO_STUDENTS = "No students found.";
    public static final String MESSAGE_INVALID_SORT = "Invalid sort type. Allowed sort type: name, progress, github.";
    public static final String MESSAGE_INVALID_SORT_ORDER = "Invalid sort order. Allowed sort order: asc, des.";
    public static final String MESSAGE_INVALID_FILTER = "Invalid filter type. Allowed filter type: course, team, "
            + "progress";
    public static final String MESSAGE_INVALID_FILTER_VALUE = "This filter value does not exists.";

    public static final List<String> VALID_SORT_ORDERS = Arrays.asList("asc", "des");
    public static final List<String> VALID_SORT_TYPES = Arrays.asList("name", "progress", "github");
    public static final List<String> VALID_FILTER_TYPES = Arrays.asList("course", "team", "progress");

    public final String sortType;
    public final String sortOrder;
    public final String filterType;
    public final String filterValue;

    /**
     * Constructor for ListCommand with sorting and filtering.
     */
    public ListCommand(String sortType, String sortOrder, String filterType, String filterValue) {
        this.sortType = sortType != null ? sortType.toLowerCase() : null;
        this.sortOrder = sortOrder != null ? sortOrder.toLowerCase() : null;
        this.filterType = filterType != null ? filterType.toLowerCase() : null;
        this.filterValue = filterValue != null ? filterValue.toLowerCase() : null;
    }

    /**
     * Constructor for ListCommand without sorting and filtering.
     */
    public ListCommand() {
        this.sortType = "";
        this.sortOrder = "";
        this.filterType = "";
        this.filterValue = "";
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        isValidFilterAndSort();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        List<Person> list = model.getFilteredPersonList();
        if (filterType != null && filterValue != null && filterType != "" && filterValue != "") {
            Predicate<Person> filter = getFilter(filterType, filterValue);
            if (filter == null) {
                return new CommandResult(MESSAGE_NO_STUDENTS);
            }
            model.updateFilteredPersonList(filter);
        }

        if (sortType != null && sortOrder != null && sortType != "" && sortOrder != "") {
            Comparator<Person> comp = this.getComparator(sortType, sortOrder);
            if (comp == null) {
                throw new CommandException(MESSAGE_INVALID_SORT);
            }
            list.sort(comp);
        }
        if (list.isEmpty()) {
            return new CommandResult(MESSAGE_NO_STUDENTS);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    private void isValidFilterAndSort() throws CommandException {
        if (sortType != null && !sortType.isEmpty() && !VALID_SORT_TYPES.contains(sortType)) {
            throw new CommandException(MESSAGE_INVALID_SORT);
        }

        if (sortOrder != null && !sortOrder.isEmpty() && !VALID_SORT_ORDERS.contains(sortOrder)) {
            throw new CommandException(MESSAGE_INVALID_SORT_ORDER);
        }

        if (filterType != null && !filterType.isEmpty() && !VALID_FILTER_TYPES.contains(filterType)) {
            throw new CommandException(MESSAGE_INVALID_FILTER);
        }
    }


    private Predicate<Person> getFilter(String filterType, String filterValue) {
        switch (filterType) {
        case "course":
            return person -> "placeholder".equals(filterValue); //temporary placeholder for person.getCourse()
        case "team":
            return person -> "placeholder".equals(filterValue); //temporary placeholder for person.getTeam()
        case "progress":
            return person -> String.valueOf(person.getProgress()).equals(filterValue);
        default:
            return person -> true;
        }
    }

    private Comparator<Person> getComparator(String sortType, String sortOrder) {
        Comparator<Person> comparator;
        switch(sortType) {
        case "name":
            comparator = Comparator.comparing(person -> person.getName().fullName);
            break;
        case "progress":
            comparator = Comparator.comparing(person -> person.getProgress().value);
            break;
        case "github":
            comparator = Comparator.comparing(person -> person.getGithub().value);
            break;
        default:
            return null;
        }

        return (sortOrder == null || sortOrder.equals("asc")) ? comparator : comparator.reversed();
    }
}
