package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

public class StudentId {

    public static final String MESSAGE_CONSTRAINTS = "Message has not been instantiated";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param studentId A valid address.
     */
    public StudentId(String studentId) {
        requireNonNull(studentId);
        checkArgument(isValidStudentId(studentId), MESSAGE_CONSTRAINTS);
        value = studentId;
    }

    /**
     * Returns if a given string is a valid studentId.
     */
    public static boolean isValidStudentId(String test) {
        return true; //implementation to be done later
    }
}
