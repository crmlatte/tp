package tassist.address.testutil;

import java.time.LocalDateTime;

import tassist.address.model.timedevents.Assignment;

/**
 * A utility class to help with building Assignment objects.
 */
public class AssignmentBuilder {
    public static final String DEFAULT_NAME = "CS2103 Assignment 1";
    public static final String DEFAULT_DESCRIPTION = "Implement a command line application";
    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(2024, 4, 1, 23, 59);

    private String name;
    private String description;
    private LocalDateTime time;

    /**
     * Creates a {@code AssignmentBuilder} with the default details.
     */
    public AssignmentBuilder() {
        name = DEFAULT_NAME;
        description = DEFAULT_DESCRIPTION;
        time = DEFAULT_TIME;
    }

    /**
     * Initializes the AssignmentBuilder with the data of {@code assignmentToCopy}.
     */
    public AssignmentBuilder(Assignment assignmentToCopy) {
        name = assignmentToCopy.getName();
        description = assignmentToCopy.getDescription();
        time = assignmentToCopy.getTime();
    }

    /**
     * Sets the {@code name} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the {@code description} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the {@code time} of the {@code Assignment} that we are building.
     */
    public AssignmentBuilder withTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    /**
     * Builds a new {@code Assignment} with the current values.
     */
    public Assignment build() {
        return new Assignment(name, description, time);
    }
}
