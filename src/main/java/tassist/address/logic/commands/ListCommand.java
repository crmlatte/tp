package tassist.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static tassist.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
    public static final String MESSAGE_MISSING_SORT_ORDER = "Please enter sort order. list s/[SORT TYPE] o/[SORT ORDER]"
            + "Allowed sort order: asc,des.";
    public static final String MESSAGE_INVALID_FILTER = "Invalid filter type. Allowed filter type: course, team, "
            + "progress";
    public static final String MESSAGE_INVALID_FILTER_VALUE = "This filter value does not exists.";
    public static final String MESSAGE_MISSING_FILTER_VALUE = "Please enter filter value. list f/[FILTER TYPE] "
            + "fv/[FILTER VALUE]";

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
            Predicate<Person> filter = getFilter(model, filterType, filterValue);
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
            model.sortFilteredPersonList(comp);
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


    private Predicate<Person> getFilter(Model model, String filterType, String filterValue) throws CommandException {
        switch (filterType) {
        case "course":
            boolean hasCourse = model.getFilteredPersonList().stream().anyMatch(person ->
                    "placeholder".equalsIgnoreCase(filterValue)); //temporary placeholder for person.getCourse().value
            if (!hasCourse) {
                throw new CommandException(MESSAGE_INVALID_FILTER_VALUE);
            }
            return person -> "placeholder".equals(filterValue); //temporary placeholder for
            // person.getCourse().value
        case "team":
            boolean hasTeam = model.getFilteredPersonList().stream()
                    .anyMatch(p -> "placeholder".equalsIgnoreCase(filterValue));
            if (!hasTeam) {
                throw new CommandException(MESSAGE_INVALID_FILTER_VALUE);
            }
            return person -> "placeholder".equals(filterValue);
            //temporary placeholder for person.getTeam().value
        case "progress":
            int filterProgress;
            try {
                filterProgress = Integer.parseInt(filterValue);
            } catch (NumberFormatException e) {
                throw new CommandException(MESSAGE_INVALID_FILTER_VALUE);
            }
            return person -> person.getProgress().value <= Integer.valueOf(filterValue);
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand)) {
            return false;
        }

        ListCommand otherCommand = (ListCommand) other;
        return Objects.equals(this.sortType, otherCommand.sortType)
                && Objects.equals(this.sortOrder, otherCommand.sortOrder)
                && Objects.equals(this.filterType, otherCommand.filterType)
                && Objects.equals(this.filterValue, otherCommand.filterValue);
    }

    @Override
    public String toString() {
        return "ListCommand{"
                + "sortType='" + sortType + '\''
                + ", sortOrder='" + sortOrder + '\''
                + ", filterType='" + filterType + '\''
                + ", filterValue='" + filterValue + '\''
                + '}';
    }
}
