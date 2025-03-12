package tassist.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents a student's Github link in the address book.
 */
public class Github {

    private static final String SPECIAL_CHARACTERS = "/:.";
    public static final String MESSAGE_CONSTRAINTS =
            "Githubs links should be of the format https://github.com/{username} "
            + "in the form [github url]/username "
            + "and adhere to the following constraints:\n"
            + "1. The github url part should only contain alphanumeric characters "
            + "and these special characters, excluding "
            + "the parentheses, (" + SPECIAL_CHARACTERS + "). "
            + "The github url part may not start or end with any special "
            + "characters, particularly in the format ' https://github.com ' .\n"
            + "2. This is followed by a '/' and then a username."
            + " The username is made up of alphanumeric characters "
            + "separated by dashes (-).\n"
            + "The username must:\n"
            + "    - be at least 2 characters long\n"
            + "    - start and end with alphanumeric characters\n"
            + "    - consist of alphanumeric characters, separated only by dashes, if any.";

    // alphanumeric and special characters
    private static final String GITHUB_URL_REGEX = "^https://github.com/";
    private static final String USERNAME_REGEX = "[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*$";
    public static final String VALIDATION_REGEX = GITHUB_URL_REGEX + USERNAME_REGEX;

    public final String value;

    /**
     * @param github link of the Student
     */
    public Github(String github) {
        requireNonNull(github);
        //checkArgument(isValidGithub(github), MESSAGE_CONSTRAINTS);
        value = github;
    }


    /**
     * Returns if a given string is a valid github link.
     */
    /*
    public static boolean isValidGithub(String test) {
        return test.matches(VALIDATION_REGEX);
    }
     */

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
        if (!(other instanceof Github)) {
            return false;
        }

        Github otherGithub = (Github) other;
        return value.equals(otherGithub.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
