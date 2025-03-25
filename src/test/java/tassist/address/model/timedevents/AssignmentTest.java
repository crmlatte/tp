package tassist.address.model.timedevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class AssignmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Assignment(null, "description", LocalDateTime.now()));
        assertThrows(NullPointerException.class, () -> new Assignment("name", null, LocalDateTime.now()));
        assertThrows(NullPointerException.class, () -> new Assignment("name", "description", null));
    }

    @Test
    public void isOverdue_overdueAssignment_returnsTrue() {
        Assignment overdueAssignment = new Assignment(
                "Overdue Assignment",
                "This assignment is overdue",
                LocalDateTime.now().minusDays(1));
        assertTrue(overdueAssignment.isOverdue());
    }

    @Test
    public void isOverdue_dueTodayAssignment_returnsTrue() {
        Assignment dueTodayAssignment = new Assignment(
                "Due Today Assignment",
                "This assignment is due today",
                LocalDateTime.now());
        assertTrue(dueTodayAssignment.isOverdue());
    }

    @Test
    public void isOverdue_futureAssignment_returnsFalse() {
        Assignment futureAssignment = new Assignment(
                "Future Assignment",
                "This assignment is in the future",
                LocalDateTime.now().plusDays(1));
        assertFalse(futureAssignment.isOverdue());
    }

    @Test
    public void calculateRemainingTime_overdueAssignment_returnsOverdue() {
        Assignment overdueAssignment = new Assignment(
                "Overdue Assignment",
                "This assignment is overdue",
                LocalDateTime.now().minusDays(1));
        assertEquals("Overdue", overdueAssignment.calculateRemainingTime());
    }

    @Test
    public void calculateRemainingTime_dueTomorrow_returnsOneDay() {
        Assignment dueTomorrowAssignment = new Assignment(
                "Due Tomorrow Assignment",
                "This assignment is due tomorrow",
                LocalDateTime.now().plusDays(1));
        assertEquals("1 day", dueTomorrowAssignment.calculateRemainingTime());
    }

    @Test
    public void calculateRemainingTime_dueInDays_returnsDays() {
        Assignment dueInDaysAssignment = new Assignment(
                "Due in Days Assignment",
                "This assignment is due in 5 days",
                LocalDateTime.now().plusDays(5));
        assertEquals("5 days", dueInDaysAssignment.calculateRemainingTime());
    }

    @Test
    public void calculateRemainingTime_dueInMonths_returnsMonthsAndDays() {
        Assignment dueInMonthsAssignment = new Assignment(
                "Due in Months Assignment",
                "This assignment is due in 2 months and 5 days",
                LocalDateTime.now().plusMonths(2).plusDays(5));
        assertEquals("2 months 5 days", dueInMonthsAssignment.calculateRemainingTime());
    }

    @Test
    public void calculateRemainingTime_dueInYears_returnsYearsMonthsAndDays() {
        Assignment dueInYearsAssignment = new Assignment(
                "Due in Years Assignment",
                "This assignment is due in 1 year, 2 months, and 5 days",
                LocalDateTime.now().plusYears(1).plusMonths(2).plusDays(5));
        assertEquals("1 year 2 months 5 days", dueInYearsAssignment.calculateRemainingTime());
    }

    @Test
    public void isSameTimedEvent_sameAssignment_returnsTrue() {
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                LocalDateTime.now());
        assertTrue(assignment.isSameTimedEvent(assignment));
    }

    @Test
    public void isSameTimedEvent_null_returnsFalse() {
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                LocalDateTime.now());
        assertFalse(assignment.isSameTimedEvent(null));
    }

    @Test
    public void isSameTimedEvent_sameNameAndTime_returnsTrue() {
        LocalDateTime time = LocalDateTime.now();
        Assignment assignment1 = new Assignment("Test Assignment", "Description 1", time);
        Assignment assignment2 = new Assignment("Test Assignment", "Description 2", time);
        assertTrue(assignment1.isSameTimedEvent(assignment2));
    }

    @Test
    public void isSameTimedEvent_differentName_returnsFalse() {
        LocalDateTime time = LocalDateTime.now();
        Assignment assignment1 = new Assignment("Test Assignment 1", "Description", time);
        Assignment assignment2 = new Assignment("Test Assignment 2", "Description", time);
        assertFalse(assignment1.isSameTimedEvent(assignment2));
    }

    @Test
    public void isSameTimedEvent_differentTime_returnsFalse() {
        Assignment assignment1 = new Assignment(
                "Test Assignment",
                "Description",
                LocalDateTime.now());
        Assignment assignment2 = new Assignment(
                "Test Assignment",
                "Description",
                LocalDateTime.now().plusDays(1));
        assertFalse(assignment1.isSameTimedEvent(assignment2));
    }

    @Test
    public void equals_sameAssignment_returnsTrue() {
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                LocalDateTime.now());
        assertTrue(assignment.equals(assignment));
    }

    @Test
    public void equals_null_returnsFalse() {
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                LocalDateTime.now());
        assertFalse(assignment.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                LocalDateTime.now());
        assertFalse(assignment.equals("Not an Assignment"));
    }

    @Test
    public void equals_differentAssignment_returnsFalse() {
        Assignment assignment1 = new Assignment(
                "Test Assignment 1",
                "Test Description 1",
                LocalDateTime.now());
        Assignment assignment2 = new Assignment(
                "Test Assignment 2",
                "Test Description 2",
                LocalDateTime.now().plusDays(1));
        assertFalse(assignment1.equals(assignment2));
    }

    @Test
    public void toString_returnsCorrectString() {
        LocalDateTime time = LocalDateTime.of(2024, 3, 15, 12, 0);
        Assignment assignment = new Assignment(
                "Test Assignment",
                "Test Description",
                time);
        assertEquals("Assignment: Test Assignment (Due: 15-03-2024)", assignment.toString());
    }
} 