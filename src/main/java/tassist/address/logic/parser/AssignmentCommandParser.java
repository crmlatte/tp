package tassist.address.logic.parser;

import static tassist.address.logic.parser.CliSyntax.PREFIX_DATE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import tassist.address.logic.Messages;
import tassist.address.logic.commands.AssignmentCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.timedevents.Assignment;

/**
 * Parses input arguments and creates a new AssignmentCommand object
 */
public class AssignmentCommandParser implements Parser<AssignmentCommand> {

    private static final String MESSAGE_INVALID_DATE = "Invalid date format. Use: dd-MM-yyyy, dd-MM-yy, or dd-MM";
    private static final String MESSAGE_DATE_IN_PAST = "Assignment date must be in the future";

    /**
     * Parses the given {@code String} of arguments in the context of the AssignmentCommand
     * and returns an AssignmentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE);
        String name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()).toString();
        LocalDateTime date = parseDate(argMultimap.getValue(PREFIX_DATE).get());

        Assignment assignment = new Assignment(name, "", date); // Empty description for now

        return new AssignmentCommand(assignment);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the date string into a LocalDateTime object.
     * Accepts formats: dd-MM-yyyy, dd-MM-yy, dd-MM (current year)
     * Throws ParseException if date is invalid or in the past.
     */
    private LocalDateTime parseDate(String dateStr) throws ParseException {
        try {
            // Try dd-MM-yyyy format
            DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(dateStr, fullFormatter);
            return validateAndCreateDateTime(date);
        } catch (DateTimeParseException e1) {
            try {
                // Try dd-MM-yy format
                DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                LocalDate date = LocalDate.parse(dateStr, shortFormatter);
                return validateAndCreateDateTime(date);
            } catch (DateTimeParseException e2) {
                try {
                    // Try dd-MM format (use current year)
                    DateTimeFormatter currentYearFormatter = DateTimeFormatter.ofPattern("dd-MM");
                    LocalDate currentDate = LocalDate.now();
                    LocalDate date = LocalDate.parse(dateStr, currentYearFormatter)
                            .withYear(currentDate.getYear());
                    return validateAndCreateDateTime(date);
                } catch (DateTimeParseException e3) {
                    throw new ParseException(MESSAGE_INVALID_DATE);
                }
            }
        }
    }

    /**
     * Validates that the date is in the future and creates a LocalDateTime with midnight time.
     * @throws ParseException if date is in the past
     */
    private LocalDateTime validateAndCreateDateTime(LocalDate date) throws ParseException {
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            throw new ParseException(MESSAGE_DATE_IN_PAST);
        }
        return LocalDateTime.of(date, LocalTime.MIDNIGHT);
    }
} 