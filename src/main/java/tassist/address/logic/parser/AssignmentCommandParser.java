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

    public static final String MESSAGE_INVALID_DATE = "Invalid date format. Use: dd-MM-yyyy, dd-MM-yy, or dd-MM";
    public static final String MESSAGE_DATE_IN_PAST = "Assignment date must start from after today";
    public static final String MESSAGE_INVALID_NAME = "Name should not be blank";
    public static final String MESSAGE_INVALID_DATE_VALUES = "Invalid date values. Please check the input is correct.";

    /**
     * Parses the given {@code String} of arguments in the context of the AssignmentCommand
     * and returns an AssignmentCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public AssignmentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignmentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE);

        String name = argMultimap.getValue(PREFIX_NAME).get();
        String dateStr = argMultimap.getValue(PREFIX_DATE).get();

        // Validate name is not blank
        if (name.trim().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_NAME);
        }

        LocalDateTime dateTime;
        try {
            dateTime = parseDateTime(dateStr);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE_VALUES);
        }

        // Check if the date is today or in the past
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.toLocalDate().plusDays(1).atStartOfDay();
        if (dateTime.isBefore(tomorrow)) {
            throw new ParseException(MESSAGE_DATE_IN_PAST);
        }

        Assignment assignment = new Assignment(name, "", dateTime); // Empty description for now
        return new AssignmentCommand(assignment);
    }

    /**
     * Parses the date string into a LocalDateTime object.
     * Supports formats: dd-MM-yyyy, dd-MM-yy, dd-MM
     */
    private LocalDateTime parseDateTime(String dateStr) throws ParseException {
        DateTimeFormatter formatter;
        LocalDate date;
        int currentYear = LocalDate.now().getYear();

        try {
            if (dateStr.matches("\\d{2}-\\d{2}-\\d{4}")) {
                // Full date format (dd-MM-yyyy)
                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                date = LocalDate.parse(dateStr, formatter);
                // Check if the parsed date matches the input date
                if (!date.format(formatter).equals(dateStr)) {
                    throw new ParseException(MESSAGE_INVALID_DATE_VALUES);
                }
            } else if (dateStr.matches("\\d{2}-\\d{2}-\\d{2}")) {
                // Short date format (dd-MM-yy)
                formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                date = LocalDate.parse(dateStr, formatter);
                // Check if the parsed date matches the input date
                if (!date.format(formatter).equals(dateStr)) {
                    throw new ParseException(MESSAGE_INVALID_DATE_VALUES);
                }
            } else if (dateStr.matches("\\d{2}-\\d{2}")) {
                // Minimal date format (dd-MM)
                formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                date = LocalDate.parse(dateStr + "-" + currentYear, formatter);
                // If the date would be in the past with current year, use next year
                if (date.isBefore(LocalDate.now())) {
                    date = date.plusYears(1);
                }
            } else {
                throw new ParseException(MESSAGE_INVALID_DATE);
            }

            return date.atTime(LocalTime.of(23, 59));
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
