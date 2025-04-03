package tassist.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        assertFalse(Email.isValidEmail("test@")); // missing domain name
        assertFalse(Email.isValidEmail("@u.nus.edu")); // missing local part
        assertFalse(Email.isValidEmail("tommy.nus.edu")); // missing '@' symbol

        assertFalse(Email.isValidEmail("T--@u.nus.edu")); // dashes not allowed
        assertFalse(Email.isValidEmail("t..t@u.nus.edu")); // consecutive periods not allowed
        assertFalse(Email.isValidEmail("t t@u.nus.edu")); // spaces not allowed
        assertFalse(Email.isValidEmail(".tom@u.nus.edu")); // start with period
        assertFalse(Email.isValidEmail("tom.@u.nus.edu")); // end with period

        assertTrue(Email.isValidEmail("tom@u.nus.edu")); // valid email
        assertTrue(Email.isValidEmail("Tom.h@u.nus.edu")); // 1 period
        assertTrue(Email.isValidEmail("t.dawg.g@u.nus.edu")); // multiple periods
    }

    @Test
    public void equals() {
        Email email = new Email("valid@u.nus.edu");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@u.nus.edu")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@u.nus.edu")));
    }
}
