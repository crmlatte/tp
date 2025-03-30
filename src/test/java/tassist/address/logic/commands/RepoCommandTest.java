package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tassist.address.model.person.Repository;

public class RepoCommandTest {

    @Test
    public void constructor_invalidRepository_throwsException() {
        String invalidRepo = "https://github.com/invalid_user/valid-repo"; // underscore in username
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Repository(invalidRepo);
        });

        assertTrue(exception.getMessage().contains(Repository.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parser_validInputs_constructsValidRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", "valid-repo");
        assertEquals("https://github.com/ValidUser/valid-repo", repo.value);
    }

    @Test
    public void parser_invalidUsername_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("invalid_user!", "valid-repo"); // underscore is invalid in username
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_invalidRepositoryName_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", "-invalidRepo"); // starts with a dash
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_nullUsername_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo(null, "valid-repo");
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_nullRepositoryName_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo("ValidUser", null);
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_bothNull_returnsNoRepository() {
        Repository repo = RepoCommand.createRepo(null, null);
        assertEquals(Repository.NO_REPOSITORY, repo.toString());
    }

    @Test
    public void parser_edgeCase_validRepoWithUnderscoreAndDot() {
        Repository repo = RepoCommand.createRepo("Group-4", "Wealth_Vault.v2");
        assertEquals("https://github.com/Group-4/Wealth_Vault.v2", repo.toString());
    }

    @Test
    public void constructor_emptyString_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Repository("");
        });

        assertTrue(exception.getMessage().contains("Repositories should be written in the format"));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new Repository(null);
        });
    }
}
