package tassist.address.logic.parser;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.AddCommand;
import tassist.address.logic.commands.GithubCommand;
import tassist.address.logic.commands.RepoCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Github;
import tassist.address.model.person.StudentId;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_FILTER_VALUE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_SORT;
import static tassist.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static tassist.address.logic.parser.CliSyntax.PREFIX_USERNAME;
import static tassist.address.model.person.StudentId.VALIDATION_REGEX;

public class RepoCommandParser {
    public RepoCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_USERNAME, PREFIX_REPOSITORY_NAME);
        String trimmedArgs = argMultimap.getPreamble().trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE));
        }
        if (!arePrefixesPresent(argMultimap, PREFIX_USERNAME, PREFIX_REPOSITORY_NAME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE));
        }

        String username = argMultimap.getValue(PREFIX_USERNAME).orElse(null);
        String repositoryName = argMultimap.getValue(PREFIX_REPOSITORY_NAME).orElse(null);

        if (username == null || username.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RepoCommand.MESSAGE_MISSING_USERNAME));
        }
        if (repositoryName == null || repositoryName.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RepoCommand.MESSAGE_MISSING_REPOSITORY_NAME));
        }


        // Try parsing as index first
        try {
            Index index = ParserUtil.parseIndex(trimmedArgs);
            return new RepoCommand(index, username, repositoryName);
        } catch (IllegalValueException e) {
            // Not a valid index — try parsing as student ID
            try {
                StudentId studentId = ParserUtil.parseStudentId(trimmedArgs);
                return new RepoCommand(studentId, username, repositoryName);
            } catch (IllegalValueException e2) {
                // Neither worked
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RepoCommand.MESSAGE_USAGE), e2);
            }
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
