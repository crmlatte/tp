package tassist.address.logic.parser;

import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER_VALUE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_SORT;

import java.util.Arrays;
import java.util.List;

import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    private static final List<String> VALID_SORT_TYPES = Arrays.asList("name", "progress", "github");
    private static final List<String> VALID_SORT_ORDERS = Arrays.asList("asc", "des");
    private static final List<String> VALID_FILTER_TYPES = Arrays.asList("course", "team", "progress");

    @Override
    public ListCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_SORT, PREFIX_ORDER, PREFIX_FILTER,
                PREFIX_FILTER_VALUE);

        String sortType = argMultimap.getValue(PREFIX_SORT).orElse(null);
        String sortOrder = argMultimap.getValue(PREFIX_ORDER).orElse(null);
        String filterType = argMultimap.getValue(PREFIX_FILTER).orElse(null);
        String filterValue = argMultimap.getValue(PREFIX_FILTER_VALUE).orElse(null);

        // Validate sort type
        if (sortType != null && !VALID_SORT_TYPES.contains(sortType.toLowerCase())) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_SORT);
        }

        // Validate sort order
        if (sortOrder != null && !VALID_SORT_ORDERS.contains(sortOrder.toLowerCase())) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_SORT_ORDER);
        }

        // Validate filter type
        if (filterType != null && !VALID_FILTER_TYPES.contains(filterType.toLowerCase())) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_FILTER);
        }

        return new ListCommand(sortType, sortOrder, filterType, filterValue);
    }
}
