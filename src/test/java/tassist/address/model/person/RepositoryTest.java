package tassist.address.model.person;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

public class RepositoryTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Github(null));
    }

    @Test
    public void constructor_invalidGithub_throwsIllegalArgumentException() {
        String invalidRepository = "//repo.";
        assertThrows(IllegalArgumentException.class, () -> new Repository(invalidRepository));
    }

    @Test
    public void isValidRepository() {
        // blank github
        assertFalse(Repository.isValidRepository("")); // empty string
        assertFalse(Repository.isValidRepository(" ")); // spaces only

        // missing parts
        assertFalse(Repository.isValidRepository("github.com")); // missing local part
        assertFalse(Repository.isValidRepository("github.com/john")); // missing https
        assertFalse(Repository.isValidRepository("john")); // missing domain name
        assertFalse(Repository.isValidRepository("https://github.com/john")); // missing repository name

        // invalid characters
        assertFalse(Repository.isValidRepository("https://github.com/$$hi")); // invalid user name
        assertFalse(Repository.isValidRepository("https://github.com/ur_l")); // underscore in user name
        assertFalse(Repository.isValidRepository("https://github.com/john/-")); // invalid username

        // valid repository
        assertTrue(Repository.isValidRepository("https://github.com/jOhn-12/tp-io")); // with dash
        assertTrue(Repository.isValidRepository("https://github.com/jOhn-12/tp.io")); // with dot
        assertTrue(Repository.isValidRepository("https://github.com/jOhn-12/tp_io")); // with underscore
        assertTrue((Repository.isValidRepository("Repository has not been initialised"))); // no_repository
    }
    @Test
    public void equals() {
        Repository repository = new Repository("https://github.com/url/repo");

        // same values -> returns true
        assertTrue(repository.equals(new Repository("https://github.com/url/repo")));

        // same object -> returns true
        assertTrue(repository.equals(repository));

        // null -> returns false
        assertFalse(repository.equals(null));

        // different types -> returns false
        assertFalse(repository.equals(5.0f));

        // different username values -> returns false
        assertFalse(repository.equals(new Repository("https://github.com/other/repo")));

        // different repository name values -> returns false
        assertFalse(repository.equals(new Repository("https://github.com/url/repo2")));
    }
}
