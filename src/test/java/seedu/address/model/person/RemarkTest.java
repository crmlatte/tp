package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    private static final String SAMPLE_REMARK = "Loves JavaFX";
    private static final String DIFFERENT_REMARK = "Prefers Swing";

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null),
                "Constructing a Remark with null should throw NullPointerException.");
    }

    @Test
    public void toString_validRemark_returnsCorrectString() {
        Remark remark = new Remark(SAMPLE_REMARK);
        assertEquals(SAMPLE_REMARK, remark.toString(), "toString() should return the exact remark value.");
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Remark remark = new Remark(SAMPLE_REMARK);
        assertEquals(remark, remark, "A Remark should be equal to itself.");
    }

    @Test
    public void equals_differentValue_returnsFalse() {
        Remark remark1 = new Remark(SAMPLE_REMARK);
        Remark remark2 = new Remark(DIFFERENT_REMARK);
        assertNotEquals(remark1, remark2, "Remarks with different values should not be equal.");
    }

    @Test
    public void hashCode_sameValue_returnsSameHash() {
        Remark remark1 = new Remark(SAMPLE_REMARK);
        Remark remark2 = new Remark(SAMPLE_REMARK);
        assertEquals(remark1.hashCode(), remark2.hashCode(), "hashCode() should be the same for identical values.");
    }
}

