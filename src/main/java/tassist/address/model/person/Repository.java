package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's Repository in TAssist.
 */
public class Repository {

    public static final String MESSAGE_CONSTRAINTS =
            "Repositories should be written in the format of https://github.com/{username}/{repository_name}\n"
            + "{username}:\n"
            + " - Made up of alphanumeric characters\n"
            + " - Separated by only dashes (-) between segments\n"
            + " - Cannot start and end with a dash\n"
            + "{repository_name}:\n"
            + " - Made up of alphanumeric characters\n"
            + " - Can contain but cannot start with dashes (-), underscores (_), and dots (.)\n"
            + " - Cannot be empty\n"
            + " - Must start and end with an alphanumeric character\n"
            + "Example:\n"
            + "https://github.com/johnny-fargo/new.repo";

    public static final String GITHUB_URL_REGEX = "^https://github\\.com/";
    public static final String USERNAME_REGEX = "[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*";
    public static final String REPOSITORY_REGEX = "[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?";

    public static final String VALIDATION_REGEX = GITHUB_URL_REGEX + USERNAME_REGEX + "/" + REPOSITORY_REGEX + "$";
    public static final String NO_REPOSITORY = "No Repository";

    public final String value;

    /**
     * @param repository link of the Student
     */
    public Repository(String repository) {
        requireNonNull(repository);
        checkArgument(isValidRepository(repository), MESSAGE_CONSTRAINTS);
        value = repository;
    }


    /**
     * Returns if a given string is a valid repository link.
     */
    public static boolean isValidRepository(String test) {
        return test.matches(VALIDATION_REGEX) || test.matches(NO_REPOSITORY);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Repository)) {
            return false;
        }

        Repository otherRepository = (Repository) other;
        return value.equals(otherRepository.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
