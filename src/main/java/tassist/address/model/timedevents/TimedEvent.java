package tassist.address.model.timedevents;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import tassist.address.commons.util.ToStringBuilder;

/**
 * Represents a TimedEvent in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public abstract class TimedEvent {
    // Identity fields
    private final String name;
    private final String description;
    private final LocalDateTime time;

    public static final String MESSAGE_NAME_CONSTRAINTS = "Name can only contain alphanumeric characters and spaces, and cannot start with a space";
    public static final String MESSAGE_DESCRIPTION_CONSTRAINTS = "Description cannot be empty or contain only whitespace";

    /**
     * Every field must be present and not null.
     */
    public TimedEvent(String name, String description, LocalDateTime time) {
        requireNonNull(name);
        requireNonNull(description);
        requireNonNull(time);

        if (!name.matches("[\\p{Alnum}][\\p{Alnum} ]*")) {
            throw new IllegalArgumentException(MESSAGE_NAME_CONSTRAINTS);
        }
        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_DESCRIPTION_CONSTRAINTS);
        }

        this.name = name;
        this.description = description;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Returns true if the event is overdue (due date is today or in the past).
     */
    public boolean isOverdue() {
        return time.toLocalDate().isBefore(LocalDateTime.now().toLocalDate()) 
            || time.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * Calculates and formats the remaining time until this event.
     * Only includes non-zero units (years, months, days) in the output.
     */
    public String calculateRemainingTime() {
        if (isOverdue()) {
            return "Overdue";
        }

        LocalDateTime now = LocalDateTime.now();
        long totalDays = ChronoUnit.DAYS.between(now.toLocalDate(), time.toLocalDate());
        long totalMonths = ChronoUnit.MONTHS.between(now.toLocalDate(), time.toLocalDate());
        long totalYears = ChronoUnit.YEARS.between(now.toLocalDate(), time.toLocalDate());

        // If the event is due tomorrow, show 1 day
        if (totalDays == 0 && time.isAfter(now)) {
            return "1 day";
        }

        StringBuilder timeLeft = new StringBuilder();
        
        // If less than a month, only show days
        if (totalMonths == 0) {
            timeLeft.append(totalDays).append(" day").append(totalDays > 1 ? "s" : "");
        }
        // If less than a year, show months and days
        else if (totalYears == 0) {
            timeLeft.append(totalMonths).append(" month").append(totalMonths > 1 ? "s" : "");
            LocalDateTime afterMonths = now.plusMonths(totalMonths);
            long remainingDays = ChronoUnit.DAYS.between(afterMonths.toLocalDate(), time.toLocalDate());
            if (remainingDays > 0) {
                timeLeft.append(" ").append(remainingDays).append(" day").append(remainingDays > 1 ? "s" : "");
            }
        }
        // If more than a year, show years, months, and days
        else {
            timeLeft.append(totalYears).append(" year").append(totalYears > 1 ? "s" : "");
            LocalDateTime afterYears = now.plusYears(totalYears);
            long remainingMonths = ChronoUnit.MONTHS.between(afterYears.toLocalDate(), time.toLocalDate());
            if (remainingMonths > 0) {
                timeLeft.append(" ").append(remainingMonths).append(" month").append(remainingMonths > 1 ? "s" : "");
            }
            LocalDateTime afterMonths = afterYears.plusMonths(remainingMonths);
            long remainingDays = ChronoUnit.DAYS.between(afterMonths.toLocalDate(), time.toLocalDate());
            if (remainingDays > 0) {
                timeLeft.append(" ").append(remainingDays).append(" day").append(remainingDays > 1 ? "s" : "");
            }
        }
        
        return timeLeft.toString();
    }

    /**
     * Returns true if both timed events have the same name and time.
     * This defines a weaker notion of equality between two timed events.
     */
    public boolean isSameTimedEvent(TimedEvent otherTimedEvent) {
        if (otherTimedEvent == this) {
            return true;
        }

        return otherTimedEvent != null
                && otherTimedEvent.getName().equals(getName())
                && otherTimedEvent.getTime().equals(getTime());
    }

    /**
     * Returns true if both timed events have the same identity and data fields.
     * This defines a stronger notion of equality between two timed events.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimedEvent)) {
            return false;
        }

        TimedEvent otherTimedEvent = (TimedEvent) other;
        return otherTimedEvent.getName().equals(getName())
                && otherTimedEvent.getDescription().equals(getDescription())
                && otherTimedEvent.getTime().equals(getTime());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("description", description)
                .add("time", time)
                .toString();
    }
} 