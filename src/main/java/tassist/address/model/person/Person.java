package tassist.address.model.person;

import static tassist.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javafx.collections.ObservableList;
import tassist.address.commons.util.ToStringBuilder;
import tassist.address.model.tag.Tag;
import tassist.address.model.timedevents.TimedEvent;
import tassist.address.model.timedevents.UniqueTimedEventList;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {
    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final StudentId studentId;
    // Data fields
    private final ClassNumber classNumber;
    private final Progress progress;
    private final Set<Tag> tags = new HashSet<>();
    private final Github github;
    private final UniqueTimedEventList timedEvents;
    private final ProjectTeam projectTeam;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, ClassNumber classNumber,
            StudentId studentId, Github github, ProjectTeam projectTeam, Set<Tag> tags, Progress progress) {
        requireAllNonNull(name, phone, email, classNumber, studentId, github, projectTeam, tags, progress);

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.classNumber = classNumber;
        this.studentId = studentId;
        this.github = github;
        this.projectTeam = projectTeam;
        this.tags.addAll(tags);
        this.progress = progress;
        this.timedEvents = new UniqueTimedEventList();
    }

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, ClassNumber classNumber,
            StudentId studentId, Github github, ProjectTeam projectTeam, Set<Tag> tags, Progress progress,
            UniqueTimedEventList timedEvents) {
        requireAllNonNull(name, phone, email, classNumber, studentId, github, tags, progress, timedEvents);

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.classNumber = classNumber;
        this.studentId = studentId;
        this.github = github;
        this.tags.addAll(tags);
        this.progress = progress;
        this.timedEvents = timedEvents;
        this.projectTeam = projectTeam;
    }


    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public ClassNumber getClassNumber() {
        return classNumber;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    public Progress getProgress() {
        return progress;
    }

    public Github getGithub() {
        return github;
    }

    public ProjectTeam getProjectTeam() {
        return projectTeam;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an unmodifiable view of the timed events list.
     * This list will not contain any duplicate timed events.
     */
    public ObservableList<TimedEvent> getTimedEvents() {
        return timedEvents.asUnmodifiableObservableList();
    }

    /**
     * Returns the underlying UniqueTimedEventList.
     */
    public UniqueTimedEventList getTimedEventsList() {
        return timedEvents;
    }

    /**
     * Adds a timed event to the person's list.
     * The timed event must not already exist in the list.
     */
    public void addTimedEvent(TimedEvent timedEvent) {
        timedEvents.add(timedEvent);
    }

    /**
     * Removes a timed event from the person's list.
     * The timed event must exist in the list.
     */
    public void removeTimedEvent(TimedEvent timedEvent) {
        timedEvents.remove(timedEvent);
    }

    /**
     * Returns true if the person has the given timed event.
     */
    public boolean hasTimedEvent(TimedEvent timedEvent) {
        return timedEvents.contains(timedEvent);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getStudentId().equals(getStudentId());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && email.equals(otherPerson.email)
                && studentId.equals(otherPerson.studentId)
                && github.equals(otherPerson.github)
                && tags.equals(otherPerson.tags)
                && timedEvents.equals(otherPerson.timedEvents);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, classNumber, studentId, github, projectTeam, tags, progress);

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("classNumber", classNumber)
                .add("studentId", studentId)
                .add("github", github)
                .add("project team", projectTeam)
                .add("tags", tags)
                .add("progress", progress)
                .add("timedEvents", timedEvents)
                .toString();
    }

}
