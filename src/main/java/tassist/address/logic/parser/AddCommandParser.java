package tassist.address.logic.parser;

import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tassist.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static tassist.address.logic.parser.CliSyntax.PREFIX_NAME;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROJECT_TEAM;
import static tassist.address.logic.parser.CliSyntax.PREFIX_REPOSITORY;
import static tassist.address.logic.parser.CliSyntax.PREFIX_STUDENT_ID;
import static tassist.address.logic.parser.CliSyntax.PREFIX_TAG;
import static tassist.address.model.person.ClassNumber.DEFAULT_CLASS;
import static tassist.address.model.person.Github.NO_GITHUB;
import static tassist.address.model.person.ProjectTeam.NO_PROJECT_TEAM;
import static tassist.address.model.person.Repository.NO_REPOSITORY;

import java.util.Set;
import java.util.stream.Stream;

import tassist.address.logic.commands.AddCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Email;
import tassist.address.model.person.Github;
import tassist.address.model.person.Name;
import tassist.address.model.person.Person;
import tassist.address.model.person.Phone;
import tassist.address.model.person.Progress;
import tassist.address.model.person.ProjectTeam;
import tassist.address.model.person.Repository;
import tassist.address.model.person.StudentId;
import tassist.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                        PREFIX_CLASS, PREFIX_STUDENT_ID, PREFIX_GITHUB, PREFIX_PROJECT_TEAM, PREFIX_REPOSITORY,
                        PREFIX_TAG, PREFIX_PROGRESS);
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_PHONE, PREFIX_STUDENT_ID, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
<<<<<<< HEAD
                PREFIX_STUDENT_ID, PREFIX_GITHUB, PREFIX_PROGRESS);
=======
                PREFIX_CLASS, PREFIX_STUDENT_ID, PREFIX_GITHUB, PREFIX_PROJECT_TEAM,
                PREFIX_REPOSITORY, PREFIX_PROGRESS);
>>>>>>> master
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        StudentId studentId = ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENT_ID).get());
        ClassNumber classNumber = ParserUtil.parseClassNumber(argMultimap.getValue(PREFIX_CLASS).orElse(DEFAULT_CLASS));
        Github github = ParserUtil.parseGithub(argMultimap.getValue(PREFIX_GITHUB).orElse(NO_GITHUB));
        ProjectTeam projectTeam = ParserUtil.parseProjectTeam(argMultimap.getValue(PREFIX_PROJECT_TEAM)
                .orElse(NO_PROJECT_TEAM));
        Repository repository = ParserUtil.parseRepository(argMultimap.getValue(PREFIX_REPOSITORY)
                .orElse(NO_REPOSITORY));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Progress progress = ParserUtil.parseProgress(argMultimap.getValue(PREFIX_PROGRESS).orElse("0"));

        Person person = new Person(name, phone, email, classNumber, studentId, github, projectTeam,
                repository, tagList, progress);
        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
