package tassist.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.commands.RepoCommand.MESSAGE_NO_INDEX_STUDENTID;
import static tassist.address.logic.commands.RepoCommand.MESSAGE_USAGE;
import static tassist.address.logic.commands.RepoCommand.MESSAGE_VALID_COMMAND;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import tassist.address.commons.core.index.Index;
import tassist.address.commons.exceptions.IllegalValueException;
import tassist.address.logic.commands.RepoCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.Repository;
import tassist.address.model.person.StudentId;

/**
 * Parses input arguments and creates a new {@code RepoCommand} object
 */
public class RepoCommandParser implements Parser<RepoCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the {@code RepoCommand}
     * and returns a {@code RepoCommand} object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RepoCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_USERNAME, PREFIX_REPOSITORY_NAME,
                PREFIX_REPOSITORY);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_USERNAME, PREFIX_REPOSITORY_NAME, PREFIX_REPOSITORY
        );
        String trimmedArgs = argMultimap.getPreamble().trim();

        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String username = argMultimap.getValue(PREFIX_USERNAME).orElse(null);
        String repositoryName = argMultimap.getValue(PREFIX_REPOSITORY_NAME).orElse(null);
        String fullRepoUrl = argMultimap.getValue(PREFIX_REPOSITORY).orElse(null);

        Repository repository = null;

        if (username != null || repositoryName != null) {
            if (username == null || !username.matches(RepoCommand.VALID_USERNAME_REGEX)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RepoCommand.MESSAGE_INVALID_USERNAME));
            }
            if (repositoryName == null || !repositoryName.matches(RepoCommand.VALID_REPOSITORY_REGEX)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RepoCommand.MESSAGE_INVALID_REPOSITORY_NAME));
            }
        } else if (fullRepoUrl != null) {
            if (!Repository.isValidRepository(fullRepoUrl)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RepoCommand.MESSAGE_INVALID_URL));
            }
            repository = new Repository(fullRepoUrl);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_VALID_COMMAND));
        }

        if (trimmedArgs.matches(StudentId.VALIDATION_REGEX)) {
            try {
                StudentId studentId = ParserUtil.parseStudentId(trimmedArgs);
                return new RepoCommand(studentId, username, repositoryName, repository);
            } catch (IllegalValueException ive) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        MESSAGE_NO_INDEX_STUDENTID), ive);
            }
        }

        try {
            Index index = ParserUtil.parseIndex(trimmedArgs);
            return new RepoCommand(index, username, repositoryName, repository);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MESSAGE_NO_INDEX_STUDENTID), ive);
        }
    }

}
