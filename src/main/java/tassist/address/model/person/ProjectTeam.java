package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's Project Team in TAssist.
 */
public class ProjectTeam {

    public static final String MESSAGE_CONSTRAINTS = "Project Team can take any values but should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs an {@code ProjectTeam}.
     *
     * @param projectTeam A valid ProjectTeam.
     */
    public ProjectTeam(String projectTeam) {
        requireNonNull(projectTeam);
        checkArgument(isValidTeam(projectTeam), MESSAGE_CONSTRAINTS);
        value = projectTeam;
    }

    /**
     * Returns true if a given string is a valid projectTeam string.
     */
    public static boolean isValidTeam(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Progress)) {
            return false;
        }

        ProjectTeam otherProjectTeam = (ProjectTeam) other;
        return this.value == otherProjectTeam.value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
