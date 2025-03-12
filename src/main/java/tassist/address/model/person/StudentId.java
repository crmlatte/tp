package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's id number.
 */
public class StudentId {

    public static final String MESSAGE_CONSTRAINTS = "Student Id should be in the form AXXXXXXXB "
            + "and adhere to the following constraints:\n"
            + "1. The student id must start and end with a letter"
            + "2. The 7 Xs represent 7 numbers in the id"
            + "Spaces are not allowed in the student id."
            + "Id cannot be null.";

    // alphanumeric and special characters
    private static final String VALIDATION_REGEX = "^[a-zA-Z]+ [0-9] +(-[a-zA-Z0-9]+)*$";
    public final String value;

    public StudentId(String studentId) {
        requireNonNull(studentId);
        checkArgument(isValidStudentId(studentId), MESSAGE_CONSTRAINTS);
        value = studentId;
    }

    /**
     * Returns if a given string is a valid Student id.
     */
    public static boolean isValidStudentId(String test) {
        return test.matches(VALIDATION_REGEX);
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
        if (!(other instanceof StudentId)) {
            return false;
        }

        StudentId otherStudentId = (StudentId) other;
        return value.equals(otherStudentId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
