package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's progress in TAssist.
 * Value must be between 0% to 100%.
 */
public class Progress {
    public final int value;
    public static final String MESSAGE_CONSTRAINTS = "Progress must be a percentage between 0 and 100.";

    /**
     * Constructs a {@code Progress}.
     *
     * @param progress_value A valid progress percentage (0-100).
     */
    public Progress(int progress_value) {
        requireNonNull(progress_value);
        checkArgument(isValidProgress(progress_value), MESSAGE_CONSTRAINTS);
        this.value = progress_value;
    }

    /**
     * Returns a boolean, showing whether the progress value is between 0 and 100.
     *
     * @param progress_value The percentage value of the progress.
     * @return boolean True if the value is between 0 and 100, false otherwise.
     */
    public static boolean isValidProgress(int progress_value) {
        if (0 <= progress_value && progress_value <= 100) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.value + "%";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Progress)) {
            return false;
        }

        Progress otherProgress = (Progress) other;
        return this.value == otherProgress.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

}
