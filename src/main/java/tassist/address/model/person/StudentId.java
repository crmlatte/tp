package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

public class StudentId {

    public static final String MESSAGE_CONSTRAINTS = "Message has not been instantiated";

    public final String value;

    public StudentId(String email) {
        requireNonNull(email);
        checkArgument(isValidStudentId(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    public static boolean isValidStudentId(String test) {
        return true; //implementation to be done later
    }
}
