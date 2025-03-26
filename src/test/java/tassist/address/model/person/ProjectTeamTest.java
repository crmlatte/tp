package tassist.address.model.person;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

public class ProjectTeamTest {
    @Test
    public void isValidStudentId() {
        // null name
        assertThrows(NullPointerException.class, () -> StudentId.isValidStudentId(null));

        // invalid name
        assertFalse(ProjectTeam.isValidProjectTeam(" ")); // empty string


        // valid name
        assertTrue(ProjectTeam.isValidProjectTeam("weatl")); // all alphabets

    }
}
