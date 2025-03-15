package tassist.address.model.person;

import static java.util.Objects.requireNonNull;
import static tassist.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's progress in TAssist.
 * Value must be between 0% to 100%.
 */
public class Progress {

    public static final String MESSAGE_CONSTRAINTS = "Progress must be an integer between 0 and 100.";
    public final int value;

    /**
     * Constructs a {@code Progress}.
     *
     * @param progressValue A valid progress percentage (0-100).
     */
    public Progress(String progressValue) {
        requireNonNull(progressValue);
        checkArgument(isValidProgress(progressValue), MESSAGE_CONSTRAINTS);
        if (progressValue.endsWith("%")) {
            progressValue = progressValue.substring(0, progressValue.length() - 1);
        }
        this.value = Integer.parseInt(progressValue);
    }

    /**
     * Returns a boolean, showing whether the progress value is between 0 and 100.
     *
     * @param progressValue The percentage value of the progress.
     * @return boolean True if the value is between 0 and 100 (with optional '%'), false otherwise.
     */
    public static boolean isValidProgress(String progressValue) {
        return progressValue.matches("^(100|[1-9]?\\d|0)(%)?$");
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
