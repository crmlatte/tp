package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tassist.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static tassist.address.logic.commands.CommandTestUtil.VALID_STUDENTID_AMY;
import static tassist.address.logic.parser.CliSyntax.PREFIX_CLASS;
import static tassist.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static tassist.address.logic.parser.CliSyntax.PREFIX_PROGRESS;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.commands.AddCommand;
import tassist.address.logic.commands.ClassCommand;
import tassist.address.logic.commands.ClearCommand;
import tassist.address.logic.commands.DeleteCommand;
import tassist.address.logic.commands.EditCommand;
import tassist.address.logic.commands.EditCommand.EditPersonDescriptor;
import tassist.address.logic.commands.ExitCommand;
import tassist.address.logic.commands.ExportCommand;
import tassist.address.logic.commands.FindCommand;
import tassist.address.logic.commands.GithubCommand;
import tassist.address.logic.commands.HelpCommand;
import tassist.address.logic.commands.ImportCommand;
import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.commands.OpenCommand;
import tassist.address.logic.commands.ProgressCommand;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.person.ClassNumber;
import tassist.address.model.person.Github;
import tassist.address.model.person.NameContainsKeywordsPredicate;
import tassist.address.model.person.Person;
import tassist.address.model.person.Progress;
import tassist.address.model.person.StudentId;
import tassist.address.testutil.EditPersonDescriptorBuilder;
import tassist.address.testutil.PersonBuilder;
import tassist.address.testutil.PersonUtil;

public class AddressBookParserTest {

    @TempDir
    public Path testRoot;

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_class() throws Exception {
        final ClassNumber classNumber = new ClassNumber("T01");
        ClassCommand command = (ClassCommand) parser.parseCommand(ClassCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_CLASS + classNumber.value);
        assertEquals(new ClassCommand(INDEX_FIRST_PERSON, classNumber), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete_index() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_delete_studentId() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + VALID_STUDENTID_AMY);
        assertEquals(new DeleteCommand(new StudentId(VALID_STUDENTID_AMY)), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_github_studentId() throws Exception {
        final String github = "https://github.com/default";
        GithubCommand command = (GithubCommand) parser.parseCommand(GithubCommand.COMMAND_WORD + " "
                + VALID_STUDENTID_AMY + " " + PREFIX_GITHUB + github);
        assertEquals(new GithubCommand(new StudentId(VALID_STUDENTID_AMY), new Github(github)), command);
    }

    @Test
    public void parseCommand_github_index() throws Exception {
        final String github = "https://github.com/default";
        GithubCommand command = (GithubCommand) parser.parseCommand(GithubCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_GITHUB + github);
        assertEquals(new GithubCommand(INDEX_FIRST_PERSON, new Github(github)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);

        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        // Simple list command
        ListCommand expectedCommand = new ListCommand(null, null, null, null);
        assertEquals(expectedCommand, parser.parseCommand("list"));

        // List with sort only
        expectedCommand = new ListCommand("name", "asc", null, null);
        assertEquals(expectedCommand, parser.parseCommand("list s/name o/asc"));

        // List with filter only
        expectedCommand = new ListCommand(null, null, "class", "T01");
        assertEquals(expectedCommand, parser.parseCommand("list f/class fv/T01"));

        // List with sort and filter
        expectedCommand = new ListCommand("progress", "des", "progress", "50");
        assertEquals(expectedCommand, parser.parseCommand("list s/progress o/des f/progress fv/50"));
    }

    @Test
    public void parseCommand_open() throws Exception {
        OpenCommand command = (OpenCommand) parser.parseCommand(
                OpenCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new OpenCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_import() throws Exception {
        // mimics absolute path
        final Path absoluteFilePath = testRoot.resolve("sample.csv");

        // creates the file so it "exists"
        if (!Files.exists(absoluteFilePath)) {
            Files.createFile(absoluteFilePath);
        }

        assertTrue(parser.parseCommand(ImportCommand.COMMAND_WORD
                + " " + absoluteFilePath.toString()) instanceof ImportCommand);
    }

    @Test
    public void parseCommand_progress() throws Exception {
        final Progress progress = new Progress("70");
        ProgressCommand command = (ProgressCommand) parser.parseCommand(ProgressCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_PROGRESS + "70");
        assertEquals(new ProgressCommand(INDEX_FIRST_PERSON, progress), command);
    }

    @Test
    public void parseCommand_export() throws Exception {
        // mimic output path
        final Path outputCsvFilePath = testRoot.resolve("output.csv");

        // creates the file so it "exists"
        if (!Files.exists(outputCsvFilePath)) {
            Files.createFile(outputCsvFilePath);
        }

        assertTrue(parser.parseCommand(ExportCommand.COMMAND_WORD
                + " " + outputCsvFilePath.toString()) instanceof ExportCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
