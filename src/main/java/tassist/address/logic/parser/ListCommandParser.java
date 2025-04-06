package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.ListCommand.MESSAGE_USAGE;
import static tassist.address.logic.commands.ListCommand.VALID_FILTER_TYPES;
import static tassist.address.logic.commands.ListCommand.VALID_SORT_ORDERS;
import static tassist.address.logic.commands.ListCommand.VALID_SORT_TYPES;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER_VALUE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_SORT;

import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Progress;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    public static final String MESSAGE_MISSING_SORT_TYPE = "Please specify a sort type to sort the list! "
            + "Refer to the usage below for how to use the `list` command:\n" + MESSAGE_USAGE;
    public static final String MESSAGE_MISSING_FILTER_TYPE = "Please specify a filter type to filter the list! "
            + "Refer to the usage below for how to use the `list` command:\n" + MESSAGE_USAGE;

    @Override
    public ListCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SORT, PREFIX_ORDER, PREFIX_FILTER,
                PREFIX_FILTER_VALUE);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, "Please use: \n" + MESSAGE_USAGE));
        }

        String sortType = argMultimap.getValue(PREFIX_SORT).orElse(null);
        String sortOrder = argMultimap.getValue(PREFIX_ORDER).orElse(null);
        String filterType = argMultimap.getValue(PREFIX_FILTER).orElse(null);
        String filterValue = argMultimap.getValue(PREFIX_FILTER_VALUE).orElse(null);

        validateSortType(sortType, sortOrder);
        validateSortOrder(sortOrder);
        validateFilterType(filterType, filterValue);
        validateFilterValue(filterValue, filterType);

        return new ListCommand(sortType, sortOrder, filterType, filterValue);
    }

    private static void validateFilterValue(String filterValue, String filterType) throws ParseException {
        if (filterValue != null && filterType != null && filterType.equalsIgnoreCase("progress")) {
            try {
                int value = Integer.parseInt(filterValue.trim());
                if (value < 0 || value > 100) {
                    throw new ParseException(Progress.MESSAGE_CONSTRAINTS);
                }
            } catch (NumberFormatException e) {
                throw new ParseException(Progress.MESSAGE_CONSTRAINTS);
            }
        }
    }

    private static void validateFilterType(String filterType, String filterValue) throws ParseException {
        if (filterType == null && filterValue != null) {
            throw new ParseException(MESSAGE_MISSING_FILTER_TYPE);
        }
        if (filterType != null) {
            if (!VALID_FILTER_TYPES.contains(filterType.toLowerCase())) {
                throw new ParseException(ListCommand.MESSAGE_INVALID_FILTER);
            }
            if (filterValue == null) {
                throw new ParseException(ListCommand.MESSAGE_MISSING_FILTER_VALUE);
            }
        }
    }

    private static void validateSortOrder(String sortOrder) throws ParseException {
        if (sortOrder != null && !VALID_SORT_ORDERS.contains(sortOrder.toLowerCase())) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_SORT_ORDER);
        }
    }

    private static void validateSortType(String sortType, String sortOrder) throws ParseException {
        if (sortType == null && sortOrder != null) {
            throw new ParseException(MESSAGE_MISSING_SORT_TYPE);
        }
        if (sortType != null) {
            if (!VALID_SORT_TYPES.contains(sortType.toLowerCase())) {
                throw new ParseException(ListCommand.MESSAGE_INVALID_SORT);
            }
            if (sortOrder == null) {
                throw new ParseException(ListCommand.MESSAGE_MISSING_SORT_ORDER);
            }
        }
    }
}
