package tassist.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GithubTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Github(null));
    }

    @Test
    public void constructor_invalidGithub_throwsIllegalArgumentException() {
        String invalidGithub = "//github.";
        assertThrows(IllegalArgumentException.class, () -> new Github(invalidGithub));
    }

    @Test
    public void isValidGithub() {
        // blank github
        assertFalse(Github.isValidGithub("")); // empty string
        assertFalse(Github.isValidGithub(" ")); // spaces only

        // missing parts
        assertFalse(Github.isValidGithub("github.com")); // missing local part
        assertFalse(Github.isValidGithub("github.com/john")); // missing https
        assertFalse(Github.isValidGithub("john")); // missing domain name
        assertFalse(Github.isValidGithub("https://github.com/$$hi")); // invalid user name
        assertFalse(Github.isValidGithub("https://github.com/ur_l")); // underscore in user name
        assertFalse(Github.isValidGithub("https://github.com/weewoo-")); // ending with dash
      
        assertTrue(Github.isValidGithub("https://github.com/jOhn-12")); // one dash
        assertTrue(Github.isValidGithub("https://github.com/a-1-2-3-d")); // multiple dash
        assertTrue(Github.isValidGithub("https://github.com/jOhn12")); // without dash
        assertTrue((Github.isValidGithub("No Github assigned")));
    }
    @Test
    public void equals() {
        Github github = new Github("https://github.com/url");

        // same values -> returns true
        assertTrue(github.equals(new Github("https://github.com/url")));

        // same object -> returns true
        assertTrue(github.equals(github));

        // null -> returns false
        assertFalse(github.equals(null));

        // different types -> returns false
        assertFalse(github.equals(5.0f));

        // different values -> returns false
        assertFalse(github.equals(new Github("https://github.com/other")));
    }
}
