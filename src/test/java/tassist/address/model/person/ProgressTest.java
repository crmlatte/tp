package tassist.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ProgressTest {

    @Test
    public void constructor_validProgressWithoutPercent_success() {
        Progress progress = new Progress("75");
        assertEquals("75%", progress.toString());
    }

    @Test
    public void constructor_validProgressWithPercent_success() {
        Progress progress = new Progress("85%");
        assertEquals("85%", progress.toString());
    }

    @Test
    public void constructor_zeroProgress_success() {
        Progress progress = new Progress("0");
        assertEquals("0%", progress.toString());
    }

    @Test
    public void constructor_maxProgress_success() {
        Progress progress = new Progress("100");
        assertEquals("100%", progress.toString());
    }

    @Test
    public void constructor_invalidProgressAbove100_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Progress("101"));
    }

    @Test
    public void constructor_invalidNegativeProgress_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Progress("-5"));
    }

    @Test
    public void constructor_invalidCharacters_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Progress("50%%"));
        assertThrows(IllegalArgumentException.class, () -> new Progress("abc"));
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        Progress progress1 = new Progress("50");
        Progress progress2 = new Progress("50%");
        assertEquals(progress1, progress2);
    }

    @Test
    public void equals_differentValue_returnsFalse() {
        Progress progress1 = new Progress("30");
        Progress progress2 = new Progress("70");
        assertNotEquals(progress1, progress2);
    }

    @Test
    public void hashCode_consistentWithEquals() {
        Progress progress1 = new Progress("90");
        Progress progress2 = new Progress("90%");
        assertEquals(progress1.hashCode(), progress2.hashCode());
    }
}
