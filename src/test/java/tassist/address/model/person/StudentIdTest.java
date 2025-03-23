package tassist.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentId(null));
    }

    @Test
    public void constructor_invalidStudentId_throwsIllegalArgumentException() {
        String invalidStudentId = "";
        assertThrows(IllegalArgumentException.class, () -> new StudentId(invalidStudentId));
    }

    @Test
    public void isValidStudentId() {
        // null name
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // invalid name
        assertFalse(StudentId.isValidStudentId("")); // empty string
        assertFalse(StudentId.isValidStudentId(" ")); // spaces only
        assertFalse(StudentId.isValidStudentId("^")); // only non-alphanumeric characters
        assertFalse(StudentId.isValidStudentId("1234567")); // only contains numeric characters
        assertFalse(StudentId.isValidStudentId("B1234567N")); // first character is not 'A'
        assertFalse(StudentId.isValidStudentId("A12345*7N")); // contains non-alphanumeric characters
        assertFalse(StudentId.isValidStudentId("A123456N")); // only contains

        // valid name
        assertTrue(StudentId.isValidStudentId("A0287670M")); // first character is 'A', followed by 7 numbers,
        // followed by an uppercase character 'M'
        assertTrue(StudentId.isValidStudentId("A0000000Z"));
    }

    @Test
    public void equals() {
        StudentId id = new StudentId("A0000000A");

        // same values -> returns true
        assertTrue(id.equals(new StudentId("A0000000A")));

        // same object -> returns true
        assertTrue(id.equals(id));

        // null -> returns false
        assertFalse(id.equals(null));

        // different types -> returns false
        assertFalse(id.equals(5.0f));

        // different values -> returns false
        assertFalse(id.equals(new StudentId("A0000000B")));
    }
}
