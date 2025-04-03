package tassist.address.testutil;

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

import java.util.Set;

import tassist.address.logic.commands.AddCommand;
import tassist.address.logic.commands.EditCommand.EditPersonDescriptor;
import tassist.address.model.person.Person;
import tassist.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + person.getName().value + " ");
        sb.append(PREFIX_PHONE + person.getPhone().value + " ");
        sb.append(PREFIX_EMAIL + person.getEmail().value + " ");
        sb.append(PREFIX_CLASS + person.getClassNumber().value + " ");
        sb.append(PREFIX_STUDENT_ID + person.getStudentId().value + " ");
        sb.append(PREFIX_GITHUB + person.getGithub().value + " ");
        sb.append(PREFIX_PROJECT_TEAM + person.getProjectTeam().value + " ");
        sb.append(PREFIX_REPOSITORY + person.getRepository().value + " ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG + s.tagName + " ")
        );
        sb.append(PREFIX_PROGRESS + String.valueOf(person.getProgress().value) + " ");
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.value).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getClassNumber().ifPresent(classNumber -> sb.append(PREFIX_CLASS).append(classNumber.value)
                .append(" "));
        descriptor.getStudentId().ifPresent(studentId -> sb.append(PREFIX_STUDENT_ID).append(studentId.value)
                .append(" "));
        descriptor.getGithub().ifPresent(github -> sb.append(PREFIX_GITHUB).append(github.value).append(" "));
        descriptor.getProjectTeam().ifPresent(projectTeam -> sb.append(PREFIX_PROJECT_TEAM).append(projectTeam.value)
                .append(" "));
        descriptor.getRepository().ifPresent(repository -> sb.append(PREFIX_REPOSITORY).append(repository.value)
                .append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(s.tagName).append(" "));
            }
        }
        descriptor.getProgress().ifPresent(progress -> sb.append(PREFIX_PROGRESS).append(progress.value)
                .append(" "));
        return sb.toString();
    }
}
